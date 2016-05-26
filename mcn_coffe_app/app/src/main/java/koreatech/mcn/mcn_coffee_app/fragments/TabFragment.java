package koreatech.mcn.mcn_coffee_app.fragments;

import android.support.v4.app.Fragment;

public class TabFragment extends Fragment {

    protected int position;

    public void setTags(int position){
        this.position = position;
    }

    public  int getTags(){
        return position;
    }
}