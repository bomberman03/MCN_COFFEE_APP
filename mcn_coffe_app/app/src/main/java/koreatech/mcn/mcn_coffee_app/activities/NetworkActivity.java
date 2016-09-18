package koreatech.mcn.mcn_coffee_app.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.afollestad.materialdialogs.MaterialDialog;

public class NetworkActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDialog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        progressDialog.dismiss();
        successDialog.dismiss();
        failureDialog.dismiss();
    }

    public void initDialog(){
        progressDialog = new MaterialDialog.Builder(this)
                .title(progressTitle)
                .content(progressContent)
                .progress(true, 0)
                .progressIndeterminateStyle(true)
                .build();
        failureDialog = new MaterialDialog.Builder(this)
                .title(failureTitle)
                .content(failureContent)
                .positiveText(positiveText)
                .build();
        successDialog = new MaterialDialog.Builder(this)
                .title(successTitle)
                .content(successContent)
                .positiveText(positiveText)
                .build();
    }
}
