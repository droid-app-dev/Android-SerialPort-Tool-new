package com.droid_app_dev.serialportcommunication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import butterknife.BindView;
import butterknife.OnClick;

import com.droid_app_dev.serialportcommunication.R;
import com.droid_app_dev.serialportcommunication.activity.base.BaseActivity;
import com.droid_app_dev.serialportcommunication.comn.SerialPortManager;
import com.droid_app_dev.serialportcommunication.model.Command;
import com.droid_app_dev.serialportcommunication.util.BaseListAdapter;
import com.droid_app_dev.serialportcommunication.util.CommandParser;
import com.droid_app_dev.serialportcommunication.util.ListViewHolder;
import com.droid_app_dev.serialportcommunication.util.ToastUtil;
import com.licheedev.myutils.LogPlus;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import java.io.File;
import java.util.List;
import ru.bartwell.exfilepicker.ExFilePicker;
import ru.bartwell.exfilepicker.data.ExFilePickerResult;

public class LoadCmdListActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    public static final int REQUEST_FILE = 233;
    @BindView(R.id.btn_load_list)
    Button mBtnLoadList;
    @BindView(R.id.list_view)
    ListView mListView;

    private ExFilePicker mFilePicker;
    private CommandParser mParser;
    private InnerAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_load_cmd_list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBar(true, "Load command list");
        initFilePickers();
        mParser = new CommandParser();
        mListView.setOnItemClickListener(this);
        mAdapter = new InnerAdapter();
        mListView.setAdapter(mAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_FILE) {
            ExFilePickerResult result = ExFilePickerResult.getFromIntent(data);
            if (result != null && result.getCount() > 0) {
                File file = new File(result.getPath(), result.getNames().get(0));

                mParser.rxParse(file).subscribe(new Observer<List<Command>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<Command> commands) {
                        mAdapter.setNewData(commands);
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogPlus.e("parsing failed", e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
            } else {
                ToastUtil.showOne(this, "no file selected");
            }
        }
    }

    /**
     */
    private void initFilePickers() {

        mFilePicker = new ExFilePicker();
        mFilePicker.setNewFolderButtonDisabled(true);
        mFilePicker.setQuitButtonEnabled(true);
        mFilePicker.setUseFirstItemAsUpEnabled(true);
        mFilePicker.setCanChooseOnlyOneItem(true);
        mFilePicker.setShowOnlyExtensions("txt");
        mFilePicker.setChoiceType(ExFilePicker.ChoiceType.FILES);
    }

    @OnClick({ R.id.btn_load_list })
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_load_list:
                mFilePicker.start(this, REQUEST_FILE);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Command item = mAdapter.getItem(position);
        SerialPortManager.instance().sendCommand(item.getCommand());
    }



    private static class InnerAdapter extends BaseListAdapter<Command> {
        @Override
        protected void inflateItem(ListViewHolder holder, int position) {

            Command item = getItem(position);

            String comment = String.valueOf(position + 1);
            comment =
                TextUtils.isEmpty(item.getComment()) ? comment : comment + " " + item.getComment();

            holder.setText(R.id.tv_comment, comment);
            holder.setText(R.id.tv_command, item.getCommand());
        }

        @Override
        public int getItemLayoutId(int viewType) {
            return R.layout.item_load_command_list;
        }
    }
}
