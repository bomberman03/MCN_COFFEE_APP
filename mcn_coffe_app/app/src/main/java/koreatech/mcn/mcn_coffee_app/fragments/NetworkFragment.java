package koreatech.mcn.mcn_coffee_app.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;

import koreatech.mcn.mcn_coffe_app.R;

/**
 * Created by blood_000 on 2016-09-27.
 */

public class NetworkFragment extends TabFragment {

    protected MaterialDialog progressDialog;
    protected MaterialDialog failureDialog;
    protected MaterialDialog successDialog;

    public void showSuccessDialog()
    {
        successDialog.show();
    }

    public void hideSuccessDialog()
    {
        successDialog.hide();
    }

    public void  showFailureDialog(String message){
        failureDialog.setContent(message);
        failureDialog.show();
    }

    public void hideFailureDialog(){
        failureDialog.hide();
    }

    public void showProgressDialog(){
        progressDialog.show();
    }

    public void hideProgressDialog(){
        progressDialog.hide();
    }

    protected String progressTitle      = "서버 요청중";
    protected String progressContent    = "잠시만 기다려주세요";

    protected String successTitle       = "서버 요청 성공";
    protected String successContent     = "성공적으로 처리되었습니다.";

    protected String failureTitle       = "서버 요청 실패";
    protected String failureContent     = "알수없는 이유로 실패하였습니다.";

    protected String positiveText       = "확인";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_list, container, false);
        initDialog();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        progressDialog.dismiss();
        successDialog.dismiss();
        failureDialog.dismiss();
    }

    public void initDialog(){
        progressDialog = new MaterialDialog.Builder(getContext())
                .title(progressTitle)
                .content(progressContent)
                .progress(true, 0)
                .progressIndeterminateStyle(true)
                .build();
        failureDialog = new MaterialDialog.Builder(getContext())
                .title(failureTitle)
                .content(failureContent)
                .positiveText(positiveText)
                .build();
        successDialog = new MaterialDialog.Builder(getContext())
                .title(successTitle)
                .content(successContent)
                .positiveText(positiveText)
                .build();
    }

}
