package com.example.user.mymp3_2;


import android.Manifest;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    static Button btnPlay, btnStop;
    TextView tvPlay, tvTime;
    SeekBar pbMP3;

    MediaPlayer mediaPlayer = new MediaPlayer();
    ArrayList<String> list = new ArrayList<String>();
    String selectedMP3;
    static final String MP3_PATH =  "/sdcard/지니/";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");

    //
    static MyDBHelper myDBHelper;
    SQLiteDatabase sqlDB;
    ArrayList<MainData> mainDataList;
    MainAdapter mainAdapter;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        recyclerView = findViewById(R.id.recyclerView);
        btnPlay = findViewById(R.id.btnPlay);
        btnStop = findViewById(R.id.btnStop);
        tvPlay = findViewById(R.id.tvPlay);
        tvTime = findViewById(R.id.tvTime);
        pbMP3 = findViewById(R.id.pbMP3);

        myDBHelper = new MyDBHelper(this);
        mainDataList = new ArrayList<>();

        Log.d("메세지","mainDataList = new ArrayList<>();");

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MODE_PRIVATE);

        Log.d("메세지","ActivityCompat.requestPermissions");
//        databaseSetting();
        mainDataListsetting();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Log.d("메세지","recyclerView.setLayoutManager(new LinearLayoutManager(this));");
        mainAdapter = new MainAdapter(this,R.layout.list_item,mainDataList);
        Log.d("메세지","mainAdapter = new MainAdapter(this,R.layout.list_item,mainDataList);");
        recyclerView.setAdapter(mainAdapter);
        Log.d("메세지","recyclerView.setAdapter(mainAdapter);");

        btnPlay.setOnClickListener(this);
        btnStop.setOnClickListener(this);

//        btnPlay.setClickable(true);
//        btnStop.setClickable(false);
        btnPlay.setEnabled(true);
        btnStop.setEnabled(false);
        pbMP3.setProgress(0);
        tvTime.setText("진행시간 : 0");
//        selectedMP3 = list.get(0);


    }//end of onCreate

    private void databaseSetting() {
        Log.d("메세지","databaseSetting 시작");
        //-----------------------
        File[] files = new File(MP3_PATH).listFiles();
        Log.d("메세지","포문 들어가기 전");
        for (File file : files) {
            sqlDB=myDBHelper.getWritableDatabase();
            String str = "없음";
            String fileName = file.getName();
            String extendName = fileName.substring(fileName.length() - 3);
            if (extendName.equals("mp3")) {
                list.add(fileName);
//                    mainDataList.add(new MainData(fileName,null,null,null));
                Log.d("메세지",fileName);
//                sqlDB.execSQL("INSERT INTO mp3TBL VALUES ( '"+ fileName + "' ,'"+ str + "','"+ str + "', "+ 0 + ");");
                sqlDB.execSQL("INSERT INTO mp3TBL(fName) VALUES('"+fileName+"');");
                Log.d("메세지","77줄");
//                sqlDB.execSQL("INSERT INTO mp3TBL VALUES ( '"
//                        + edtName.getText().toString() + "' , "
//                        + edtNumber.getText().toString() + ");");
                sqlDB.close();
            }


        }//end of for-----------------------------------
    }


    public void mainDataListsetting() {
        sqlDB=myDBHelper.getWritableDatabase();
        Cursor cursor;
//        cursor = sqlDB.rawQuery("SELECT * FROM mp3TBL ORDER BY gNumber ASC;", null);
        cursor = sqlDB.rawQuery("SELECT * FROM mp3TBL ORDER BY fStar ASC;", null);

//                String strNames = "그룹이름" + "\r\n" + "--------" + "\r\n";
//                String strNumbers = "인원" + "\r\n" + "--------" + "\r\n";
        mainDataList.removeAll(mainDataList);
        while (cursor.moveToNext()) {
//                    strNames += cursor.getString(0) + "\r\n";
//                    strNumbers += cursor.getString(1) + "\r\n";
            mainDataList.add(new MainData(cursor.getString(0),null,null,null));
        }
//        mainAdapter.notifyDataSetChanged();
        cursor.close();
        sqlDB.close();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnPlay:
                try {
                    selectedMP3=mainAdapter.selectFileNameString;
                    mediaPlayer.setDataSource(MP3_PATH + selectedMP3);
                    mediaPlayer.prepare();
                    mediaPlayer.start();

//                    btnPlay.setClickable(false);
//                    btnStop.setClickable(true);
                    btnPlay.setEnabled(false);
                    btnStop.setEnabled(true);
                    tvPlay.setText("실행중인 음악명:" + selectedMP3);

                    Thread thread = new Thread() {


                        @Override
                        public void run() {
                            if (mediaPlayer == null) {
                                return;
                            }
                            //1.노래에 총 걸리는 시간                             //재생시간
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tvTime.setText(selectedMP3 + " 재생시간 " + mediaPlayer.getDuration());
                                    pbMP3.setMax(mediaPlayer.getDuration());
                                }
                            });


                            while (mediaPlayer.isPlaying()) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        //자기가 진행되는 시간
                                        pbMP3.setProgress(mediaPlayer.getCurrentPosition());
                                        tvTime.setText(selectedMP3 + "진행시간 : " + simpleDateFormat.format(mediaPlayer.getCurrentPosition()));
                                    }
                                });//end of runOnUiThread() 스레드안에서 화면위젯변경을 할 수 있는 스레드

                                SystemClock.sleep(200);
                            }//end of while
                        }
                    };

                    thread.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;
            case R.id.btnStop:
                mediaPlayer.stop();
                mediaPlayer.reset();
//                btnPlay.setClickable(true);
//                btnStop.setClickable(false);
                btnPlay.setEnabled(true);
                btnStop.setEnabled(false);
                tvPlay.setText("음악");
                pbMP3.setProgress(0);
                tvTime.setText("진행시간 : 0");

                break;
        }
    }

    public class MyDBHelper extends SQLiteOpenHelper {

        public MyDBHelper(Context context) {

            super(context, "mp3DB5", null, 1);


            Log.d("메세지","마이 디비 헬퍼 생성자");
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {

            sqLiteDatabase.execSQL("CREATE TABLE mp3TBL (fName CHAR(300) PRIMARY KEY, fSinger CHAR(20),fGenre CHAR(20), fStar INTEGER);");
            Log.d("메세지","마이 디비 헬퍼 온크리에이트 테이블 생성");

        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS mp3TBL");
            onCreate(sqLiteDatabase);
        }
        public void delete(String name){
            sqlDB=myDBHelper.getWritableDatabase();

            sqlDB.execSQL("DELETE FROM mp3TBL WHERE fName = '"
                            + name + "';");

            sqlDB.close();

            Toast.makeText(getApplicationContext(), name+" 삭제됨",
                        Toast.LENGTH_SHORT).show();
            mainAdapter.notifyDataSetChanged();
        }
    }
}
