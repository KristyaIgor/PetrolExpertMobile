package md.intelectsoft.petrolmpos.Utils;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ViewPageAdapter extends FragmentPagerAdapter {
    public ViewPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position== 0){
            TabConection one= new TabConection();
            return one;
        }else{
            TabOther two= new TabOther();
            return  two;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}