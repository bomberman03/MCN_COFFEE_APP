package koreatech.mcn.mcn_coffee_app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.readystatesoftware.viewbadger.BadgeView;

import java.util.ArrayList;
import java.util.HashMap;

import koreatech.mcn.mcn_coffe_app.R;
import koreatech.mcn.mcn_coffee_app.fragments.CommentFragment;
import koreatech.mcn.mcn_coffee_app.fragments.MenuFragment;
import koreatech.mcn.mcn_coffee_app.fragments.OrderFragment;
import koreatech.mcn.mcn_coffee_app.fragments.TabFragment;
import koreatech.mcn.mcn_coffee_app.models.Cafe;
import koreatech.mcn.mcn_coffee_app.models.Order;

/**
 * Created by blood_000 on 2016-05-24.
 */
public class OrderActivity extends AppCompatActivity{

    private final int[] image_default_arr = {
            R.mipmap.order_default,
            R.mipmap.menu_default,
            R.mipmap.comment_default
    };
    private final int[] image_color_arr = {
            R.mipmap.order_color,
            R.mipmap.menu_color,
            R.mipmap.comment_color
    };

    private final int TAB_SIZE = 3;

    private HashMap<Integer, TabFragment> mPageReferenceMap = new HashMap<>();
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private ArrayList<BadgeView> badgeViews = new ArrayList<>();

    private ViewPagerAdapter mAdapter ;
    private ViewPager mViewPager;
    private SmartTabLayout mViewPagerTab;

    private MenuFragment menuFragment;
    private OrderFragment orderFragment;
    private CommentFragment commentFragment;

    private int cur_position = 1;
    private Cafe cafe;

    public void initFragments(){
        menuFragment = new MenuFragment();
        orderFragment = new OrderFragment();
        commentFragment = new CommentFragment();

        fragments.add(orderFragment);
        fragments.add(menuFragment);
        fragments.add(commentFragment);
    }

    public void initAdapter(){
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);

        mAdapter = new ViewPagerAdapter(getSupportFragmentManager(), fragments);

        mViewPager.setAdapter(mAdapter);
        mViewPagerTab.setViewPager(mViewPager);

        for(int i=0; i<TAB_SIZE; i++) {
            LinearLayout linearLayout = (LinearLayout) mViewPagerTab.getTabAt(i);
            ImageView imageView = (ImageView) linearLayout.findViewById(R.id.imageView);
            BadgeView badge = new BadgeView(this, imageView);
            badge.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
            badge.setText("1");
            badge.hide();
            badgeViews.add(badge);
            if(i!=cur_position) imageView.setImageResource(image_default_arr[i]);
            else imageView.setImageResource(image_color_arr[i]);
        }

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            public void deactivateTab(int position){
                LinearLayout linearLayout = (LinearLayout) mViewPagerTab.getTabAt(position);
                ImageView imageView = (ImageView) linearLayout.findViewById(R.id.imageView);
                imageView.setImageResource(image_default_arr[position]);
            }

            public void activateTab(int position){
                deactivateTab(cur_position);
                LinearLayout linearLayout = (LinearLayout) mViewPagerTab.getTabAt(position);
                ImageView imageView = (ImageView) linearLayout.findViewById(R.id.imageView);
                imageView.setImageResource(image_color_arr[position]);
                cur_position = position;
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                activateTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        mViewPager.setCurrentItem(cur_position);
    }

    public void routeOrder(Order order){
        orderFragment.addOrder(order);
    }

    public void updateOrder(int size){
        if(size > 0) {
            badgeViews.get(0).setText(String.valueOf(size));
            badgeViews.get(0).show();
        } else {
            badgeViews.get(0).hide();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        Intent intent = getIntent();
        cafe = (Cafe) intent.getSerializableExtra("cafe");
        getSupportActionBar().setTitle(cafe.name);
        initFragments();
        initAdapter();
    }

    public class ViewPagerAdapter extends FragmentStatePagerAdapter {

        private ArrayList<Fragment> fList;

        public ViewPagerAdapter(FragmentManager fm, ArrayList<Fragment> fList) {
            super(fm);
            this.fList = fList;
        }

        public Fragment getItem(int position) {
            return fList.get(position);
        }

        @Override
        public int getCount() {
            return fList.size();
        }

        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
            mPageReferenceMap.remove(position);
        }

        public int getItemPosition(Object item) {
            TabFragment fragment = (TabFragment) item;
            int position = fragment.getTags();
            if (position >= 0) {
                return position;
            } else {
                return POSITION_NONE;
            }
        }
    }

    public TabFragment getFragment(int key) {
        return mPageReferenceMap.get(key);
    }

    public Cafe getCafe() {
        return cafe;
    }
}
