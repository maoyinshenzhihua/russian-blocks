package com.poopblock.russian;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GameRecordActivity extends AppCompatActivity {

    private ListView recordList;
    private Button backBtn;
    private Button clearRecordBtn;
    private List<GameRecord> records;
    private RecordAdapter adapter;

    // SharedPreferences键名
    private static final String PREF_NAME = "game_records";
    private static final String KEY_RECORD_COUNT = "record_count";
    private static final String KEY_RECORD_PREFIX = "record_";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_record_activity);

        // 初始化音效管理器
        SharedPreferences sharedPreferences = getSharedPreferences("game_settings", MODE_PRIVATE);
        boolean isGameSoundEnabled = sharedPreferences.getBoolean("game_sound_enabled", true);
        SoundManager.getInstance().init(this, isGameSoundEnabled);

        // 绑定控件
        recordList = findViewById(R.id.record_list);
        backBtn = findViewById(R.id.back_btn);
        clearRecordBtn = findViewById(R.id.clear_record_btn);

        // 加载游戏记录
        loadRecords();

        // 初始化适配器
        adapter = new RecordAdapter(this, records);
        recordList.setAdapter(adapter);

        // 设置返回按钮点击事件
        backBtn.setOnClickListener(v -> {
            // 播放点击音效
            SoundManager.getInstance().playValidClickSound();
            finish();
            // 检查并应用转场动画
            applyTransitionAnimation();
        });

        // 设置清空记录按钮点击事件
        clearRecordBtn.setOnClickListener(v -> {
            // 播放点击音效
            SoundManager.getInstance().playValidClickSound();
            clearAllRecords();
        });
        
        // 为所有按钮添加焦点监听，实现手柄导航高亮
        addButtonFocusListeners();
    }
    
    /**
     * 为所有按钮添加焦点监听
     */
    private void addButtonFocusListeners() {
        View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // 获得焦点，显示蓝色边框
                    v.setBackgroundResource(R.drawable.btn_blue_border);
                    // 播放点击音效，提供听觉反馈
                    SoundManager.getInstance().playValidClickSound();
                } else {
                    // 失去焦点，恢复默认样式
                    v.setBackgroundResource(R.drawable.btn_white_black_border);
                }
            }
        };
        
        // 为所有按钮添加焦点监听
        backBtn.setOnFocusChangeListener(focusChangeListener);
        clearRecordBtn.setOnFocusChangeListener(focusChangeListener);
        
        // 设置初始焦点
        backBtn.requestFocus();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // 进入游戏记录页面时，根据用户设置决定是否启动音乐
        android.content.SharedPreferences sharedPreferences = getSharedPreferences("game_settings", MODE_PRIVATE);
        boolean isMusicEnabled = sharedPreferences.getBoolean("music_enabled", true);
        if (isMusicEnabled) {
            startService(new Intent(this, MusicService.class));
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        // 离开游戏记录页面时不停止音乐，让返回的页面处理
    }
    


    /**
     * 加载游戏记录
     */
    private void loadRecords() {
        records = new ArrayList<>();
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        int recordCount = sharedPreferences.getInt(KEY_RECORD_COUNT, 0);

        for (int i = 0; i < recordCount; i++) {
            String recordString = sharedPreferences.getString(KEY_RECORD_PREFIX + i, "");
            if (!recordString.isEmpty()) {
                GameRecord record = GameRecord.fromString(recordString);
                records.add(record);
            }
        }

        // 按分数降序排序
        Collections.sort(records, new Comparator<GameRecord>() {
            @Override
            public int compare(GameRecord r1, GameRecord r2) {
                return Integer.compare(r2.getScore(), r1.getScore());
            }
        });
    }

    /**
     * 清空所有游戏记录
     */
    private void clearAllRecords() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // 清空列表并更新适配器
        records.clear();
        adapter.notifyDataSetChanged();

        Toast.makeText(this, "记录已清空", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // 检查并应用转场动画
        applyTransitionAnimation();
    }
    
    /**
     * 检查并应用转场动画
     */
    private void applyTransitionAnimation() {
        // 从SharedPreferences获取动画设置
        android.content.SharedPreferences sharedPreferences = getSharedPreferences("game_settings", MODE_PRIVATE);
        boolean isAnimationEnabled = sharedPreferences.getBoolean("animation_enabled", false);
        if (isAnimationEnabled) {
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } else {
            // 明确指定无动画
            overridePendingTransition(0, 0);
        }
    }

    /**
     * 游戏记录适配器
     */
    private class RecordAdapter extends ArrayAdapter<GameRecord> {

        public RecordAdapter(Context context, List<GameRecord> records) {
            super(context, 0, records);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            GameRecord record = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.record_item, parent, false);
            }

            // 绑定数据
            TextView rankText = convertView.findViewById(R.id.rank_text);
            TextView scoreText = convertView.findViewById(R.id.score_text);
            TextView timeText = convertView.findViewById(R.id.time_text);
            TextView dateText = convertView.findViewById(R.id.date_text);

            rankText.setText(String.valueOf(position + 1));
            scoreText.setText(String.valueOf(record.getScore()));
            timeText.setText(record.getTime());
            dateText.setText(record.getDate());

            return convertView;
        }
    }

    /**
     * 游戏记录数据类
     */
    public static class GameRecord {
        private int score;
        private String time;
        private String date;

        public GameRecord(int score, String time, String date) {
            this.score = score;
            this.time = time;
            this.date = date;
        }

        public int getScore() {
            return score;
        }

        public String getTime() {
            return time;
        }

        public String getDate() {
            return date;
        }

        // 从字符串解析游戏记录
        public static GameRecord fromString(String recordString) {
            String[] parts = recordString.split("\\|");
            if (parts.length == 3) {
                int score = Integer.parseInt(parts[0]);
                String time = parts[1];
                String date = parts[2];
                return new GameRecord(score, time, date);
            }
            return null;
        }

        // 转换为字符串以便存储
        @Override
        public String toString() {
            return score + "|" + time + "|" + date;
        }
    }

    /**
     * 保存游戏记录
     */
    public static void saveRecord(Context context, int score, String time, String date) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        int recordCount = sharedPreferences.getInt(KEY_RECORD_COUNT, 0);

        // 创建新记录
        GameRecord record = new GameRecord(score, time, date);

        // 保存记录
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_RECORD_PREFIX + recordCount, record.toString());
        editor.putInt(KEY_RECORD_COUNT, recordCount + 1);
        editor.apply();
    }
}