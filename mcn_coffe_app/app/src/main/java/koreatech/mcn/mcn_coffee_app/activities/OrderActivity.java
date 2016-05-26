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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import koreatech.mcn.mcn_coffe_app.R;
import koreatech.mcn.mcn_coffee_app.fragments.MenuFragment;
import koreatech.mcn.mcn_coffee_app.fragments.OrderFragment;
import koreatech.mcn.mcn_coffee_app.fragments.OrderListFragment;
import koreatech.mcn.mcn_coffee_app.fragments.TabFragment;
import koreatech.mcn.mcn_coffee_app.models.Cafe;

/**
 * Created by blood_000 on 2016-05-24.
 */
public class OrderActivity extends AppCompatActivity{

    private HashMap<Integer, TabFragment> mPageReferenceMap ;
    private ViewPagerAdapter mAdapter ;
    private ViewPager mViewPager;
    private SmartTabLayout mViewPagerTab;
    private ArrayList<String> mList;
    private ArrayList<Fragment> fList;

    private int[] image_default_arr = {R.mipmap.menu_default, R.mipmap.order_default, R.mipmap.order_list_default};
    private int[] image_color_arr = { R.mipmap.menu_color, R.mipmap.order_color, R.mipmap.order_list_color };

    private int cur_position = 0;

    public Cafe getCafe() {
        return cafe;
    }

    private Cafe cafe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        Intent intent = getIntent();
        cafe = (Cafe) intent.getSerializableExtra("cafe");

        mPageReferenceMap = new HashMap<>();
        mList = new ArrayList<>();
        fList = new ArrayList<>();

        int size = 3;
        for (int i = 0; i < size; i++) {
            mList.add(String.valueOf(i));
        }
        fList.add(new MenuFragment());
        fList.add(new OrderFragment());
        fList.add(new OrderListFragment());

        mAdapter = new ViewPagerAdapter(getSupportFragmentManager(), mList, fList);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setAdapter(mAdapter);
        mViewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);
        mViewPagerTab.setViewPager(mViewPager);

        for(int i=0; i<3; i++) {
            LinearLayout linearLayout = (LinearLayout) mViewPagerTab.getTabAt(i);
            ImageView imageView = (ImageView) linearLayout.findViewById(R.id.imageView);
            if(i>0) imageView.setImageResource(image_default_arr[i]);
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
                final TabFragment tab_Fragment = getFragment(mViewPager.getCurrentItem());
                final String str = mList.get(mViewPager.getCurrentItem());
                activateTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    public TabFragment getFragment(int key) {
        return mPageReferenceMap.get(key);
    }

    public class ViewPagerAdapter extends FragmentStatePagerAdapter {

        private List<String> mList;
        private List<Fragment> fList;

        public ViewPagerAdapter(FragmentManager fm, List<String> mlist, List<Fragment> fList) {
            super(fm);
            this.mList = mlist;
            this.fList = fList;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String  title = mList.get(position);
            return title;
        }

        public int getCount() {
            return mList.size();
        }

        public Fragment getItem(int position) {
            return fList.get(position);
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
}
