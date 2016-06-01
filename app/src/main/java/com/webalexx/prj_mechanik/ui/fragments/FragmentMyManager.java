package com.webalexx.prj_mechanik.ui.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;

import com.webalexx.prj_mechanik.ui.MainActivity;

import java.io.FileDescriptor;
import java.io.PrintWriter;

/**
 * Created by web-w on 14.04.2016.
 */
public class FragmentMyManager {

    MainActivity mContext;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mTransaction;
    private int intLayoutId;
    String strFragmentName;


    public FragmentMyManager(MainActivity mainActivity) {
        this.mContext = (MainActivity) mainActivity;
        mFragmentManager = mContext.getFragmentManager();
    }

    public void setLayoutId(int intLayoutId) {
        this.intLayoutId = intLayoutId;
    }

    public void addFragment(Fragment mFragment) {
        mTransaction = mFragmentManager.beginTransaction();
        strFragmentName = mFragment.getClass().getName().toString();
        mTransaction.replace(intLayoutId, mFragment);
        mTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        mTransaction.addToBackStack(strFragmentName);
        mTransaction.commit();
        Log.v(null, ">>>>>" + mFragmentManager.getBackStackEntryCount() + "");
    }


    public void removeFragment() {
        mFragmentManager.popBackStackImmediate();
        Log.v(null, ">>>>" + mFragmentManager.getBackStackEntryCount() + "");
    }


    public int getBackStackCount() {
        return mFragmentManager.getBackStackEntryCount();
    }


}
