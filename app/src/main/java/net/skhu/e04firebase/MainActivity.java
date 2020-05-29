package net.skhu.e04firebase;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static final int RC_LOGIN = 1; //  로그인 액티비티 호출을 구별하기 위한 request code이다.
    FirebaseUser currentUser = null; // 현재 사용자
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.textView_userName);
        showUserName();
    }

    public void button_clicked(View view) {
        Class classObj = null;
        switch (view.getId()) {
            case R.id.button1: classObj = Firebase1Activity.class; break;
            case R.id.button2: classObj = MemoList1Activity.class; break;
            case R.id.button3: classObj = MemoList2Activity.class; break;
            case R.id.button4: classObj = MemoList3Activity.class; break;
        }
        Intent intent = new Intent(this, classObj);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu); // 메뉴 생성
        MenuItem menuItem_login = menu.findItem(R.id.action_login); // 로그인 메뉴
        MenuItem menuItem_logout = menu.findItem(R.id.action_logout); // 로그아웃 메뉴
        menuItem_login.setVisible(currentUser == null); // 로그아웃 상태이면 로그인 메뉴가 보임
        menuItem_logout.setVisible(currentUser != null); // 로그인 상태라면 로그아웃 메뉴가 보임
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_login) { // 로그인 메뉴 클릭
            startLoginInActivity();
            return true;
        } else if (id == R.id.action_logout) { // 로그아웃 메뉴 클릭
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // 로그인 액티비티를 호출하는 메소드이다.
    void startLoginInActivity() {
        // 이메일 인증과 구글 계정 인증을 사용하여 로그인 가능하도록 설정한다.
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(), // 이메일 로그인 기능을 사용한다
                new AuthUI.IdpConfig.GoogleBuilder().build()); // google 계정 로그인 기능을 사용한다

        // 로그인(sign in) 액티비티를 호출한다.
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_LOGIN); // 로그인 액티비티 호출을 구별하기 위한 request code 이다.
    }

    // 화면에 현재 사용자 이름을 표시한다.
    void showUserName() {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();  // 현재 사용자 객체를 구한다.
        if (currentUser != null) // 로그인했다면, 사용자 이름 출력
            textView.setText(currentUser.getDisplayName());
        else // 로그인하지 않았다면, "Anonymous" 출력
            textView.setText("Anonymous");
    }

    // 로그아웃한다
    void logout() {
        FirebaseAuth.getInstance().signOut();
        showUserName(); // 화면에 표시된 사용자 이름을 다시 출력한다.
        invalidateOptionsMenu(); // 메뉴를 상태를 변경해야 함
    }

    // startActivityForResult 메소드로 호출된 액티비티로부터 전달된 결과를 받기위한 메소드이다.
    // 파라미터 변수:
    //   requestCode: startActivityForResult 메소드를 호출할 때 전달한 호출 식별 번호이다.
    //   resultCode:  호출된 액티비티의 실행 결과 값이다. (RESULT_OK, RESULT_CANCELED)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == RC_LOGIN) { // 로그인 액티비티 호출 결과
            IdpResponse response = IdpResponse.fromResultIntent(intent);
            if (resultCode == RESULT_OK) {  // 로그인 작업이 성공인 경우
                Toast.makeText(MainActivity.this, "로그인 성공", Toast.LENGTH_LONG).show();
            } else {
                // 로그인 작업이 실패한 경우
                String message = "Authentication failure. " + response.getError().getErrorCode()
                        + " " + response.getError().getMessage();
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
            }
            showUserName(); // 화면에 표시된 사용자 이름을 다시 출력한다.
            invalidateOptionsMenu(); // 메뉴를 상태를 변경해야 함
        }
    }
}
