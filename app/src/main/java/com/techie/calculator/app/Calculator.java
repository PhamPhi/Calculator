package com.techie.calculator.app;

import android.app.Activity;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupMenu;

import com.techie.calculator.app.controllers.LogicController;
import com.techie.calculator.app.models.History;
import com.techie.calculator.app.models.HistoryAdapter;
import com.techie.calculator.app.models.PersistData;
import com.techie.calculator.app.views.CalculatorDisplay;
import com.techie.calculator.app.views.PanelSwitcher;
import android.widget.PopupMenu.OnMenuItemClickListener;


public class Calculator extends Activity implements PanelSwitcher.Listener, LogicController.Listener,
        View.OnClickListener, OnMenuItemClickListener{
    private CalculatorDisplay mDisplay;
    private PersistData mData;
    private History mHistory;
    private LogicController mController;
    private ViewPager mPager;
    private View mClearButton;
    private View mBackspaceButton;
    private View mOverflowMenuButton;

    static final int BASIC_PANEL= 0;
    static final int ADVANCED_PANEL= 1;

    private static final String LOG_TAG= "Calculator";
    private static final boolean DEBUG= false;
    private static final boolean LOG_ENABLED= false;
    private static final String STATE_CURRENT_VIEW= "state.current.view";

    public EventListener mListener = new EventListener();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM, WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        setContentView(R.layout.activity_calculator);

        mPager = (ViewPager) findViewById(R.id.panelSwitcher);
        if (mPager != null){
            mPager.setAdapter(new PageAdapter(mPager));
        }else{
            final TypedArray buttons = getResources().obtainTypedArray(R.array.buttons);
            for (int i=0; i< buttons.length(); i++){
                setOnClickListener(null, buttons.getResourceId(i, 0));
            }
            buttons.recycle();
        }
        if (mClearButton == null){
            mClearButton = findViewById(R.id.clear);
            mClearButton.setOnClickListener(mListener);
            mClearButton.setOnLongClickListener(mListener);
        }
        if (mBackspaceButton == null){
            mBackspaceButton = findViewById(R.id.del);
            mBackspaceButton.setOnClickListener(mListener);
            mBackspaceButton.setOnLongClickListener(mListener);
        }

        mData = new PersistData(this);
        mData.load();
        mHistory = mData.history;
        mDisplay = (CalculatorDisplay) findViewById(R.id.display);
        mController = new LogicController(this,mHistory, mDisplay);
        mController.setListener(this);

        mController.setDeleteMode(mData.getDeleteMode());
        mController.setLineLength(mDisplay.getMaxDigits());

        HistoryAdapter historyAdapter = new HistoryAdapter(this, mHistory, mController);
        mHistory.setObserver(historyAdapter);

        if (mPager != null){
            mPager.setCurrentItem(savedInstanceState == null ? 0 : savedInstanceState.getInt(STATE_CURRENT_VIEW, 0));
        }
        mListener.setHandler(mController, mPager);
        mDisplay.setOnKeyListener(mListener);

        if (!ViewConfiguration.get(this).hasPermanentMenuKey()){
            createFakeMenu();
        }
        mController.resumeWithHistory();
        updateDeleteModel();
    }

    private void updateDeleteModel(){
        if (mController.getDeleteMode() == Constant.DELETE_MODE_BACKSPACE){
            mClearButton.setVisibility(View.GONE);
            mBackspaceButton.setVisibility(View.VISIBLE);
        }else{
            mClearButton.setVisibility(View.VISIBLE);
            mBackspaceButton.setVisibility(View.GONE);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.calculator, menu);
        return true;
    }

    public void setOnClickListener(View root, int id){
        final View target = root != null ? root.findViewById(id) : findViewById(id);
        target.setOnClickListener(mListener);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.basic).setVisible(!getBasicVisibility());
        menu.findItem(R.id.advanced).setVisible(!getAdvancedVisibility());
        return true;
    }
    private void createFakeMenu(){
        mOverflowMenuButton = findViewById(R.id.overflow_menu);
        if (mOverflowMenuButton != null){
            mOverflowMenuButton.setVisibility(View.VISIBLE);
            mOverflowMenuButton.setOnClickListener(this);
        }
    }

    private PopupMenu constructPopupMenu(){
        final PopupMenu popupMenu = new PopupMenu(this, mOverflowMenuButton);
        mOverflowMenuButton.setOnTouchListener(popupMenu.getDragToOpenListener());
        final Menu menu = popupMenu.getMenu();
        popupMenu.inflate(R.menu.calculator);
        popupMenu.setOnMenuItemClickListener(this);
        onPrepareOptionsMenu(menu);
        return popupMenu;
    }
    private boolean getBasicVisibility(){
        return mPager != null && mPager.getCurrentItem() == BASIC_PANEL;
    }

    private boolean getAdvancedVisibility(){
        return mPager != null && mPager.getCurrentItem() == ADVANCED_PANEL;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (item.getItemId()){
            case R.id.action_settings:
                return true;
            case R.id.basic:
                if (!getBasicVisibility() && mPager != null){
                    mPager.setCurrentItem(BASIC_PANEL, true);
                }
                break;
            case R.id.advanced:
                if (!getAdvancedVisibility() && mPager != null){
                    mPager.setCurrentItem(ADVANCED_PANEL, true);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mPager != null){
            outState.putInt(STATE_CURRENT_VIEW, mPager.getCurrentItem());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mController.updateHistory();
        mData.setDeleteMode(mController.getDeleteMode());
        mData.save();
    }

    @Override
    public void onChanged() {
        invalidateOptionsMenu();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.overflow_menu:
                PopupMenu menu = constructPopupMenu();
                if (menu != null){
                    menu.show();
                }
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && getAdvancedVisibility() && mPager != null){
            mPager.setCurrentItem(BASIC_PANEL);
            return true;
        }else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return onOptionsItemSelected(item);
    }

    @Override
    public void onDeleteModeChanged() {
        updateDeleteModel();
    }

    class PageAdapter extends PagerAdapter{
        private View mSimplePage;
        private View mAdvancedPage;

        public PageAdapter(ViewPager parent){
            final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            final View simplePage = inflater.inflate(R.layout.simple_pad,parent, false);
            final View advancedPage = inflater.inflate(R.layout.advanced_pad, parent, false);
            mSimplePage = simplePage;
            mAdvancedPage = advancedPage;

            final Resources res = getResources();
            final TypedArray simpleButtons = res.obtainTypedArray(R.array.simple_buttons);
            for (int i=0; i < simpleButtons.length(); i++){
                setOnClickListener(simplePage, simpleButtons.getResourceId(i, 0));
            }
            simpleButtons.recycle();
            final TypedArray advancedButtons = res.obtainTypedArray(R.array.advanced_buttons);
            for (int i = 0; i< advancedButtons.length(); i++){
                setOnClickListener(advancedPage, simpleButtons.getResourceId(i, 0));
            }
            advancedButtons.recycle();

            final View clearButton = simplePage.findViewById(R.id.clear);
            if (clearButton != null){
                mClearButton= clearButton;
            }

            final View backspaceButton = simplePage.findViewById(R.id.del);
            if (backspaceButton != null){
                mBackspaceButton = backspaceButton;
            }
        }
        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public void startUpdate(View container) {
            super.startUpdate(container);
        }

        @Override
        public Object instantiateItem(View container, int position) {
            final View page = position == 0 ? mSimplePage : mAdvancedPage;
            ((ViewGroup) container).addView(page);
            return page;
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewGroup) container).removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void finishUpdate(View container) {
            super.finishUpdate(container);
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
            super.restoreState(state, loader);
        }
    }
}
