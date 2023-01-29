package com.github.tvbox.osc.ui.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.github.tvbox.osc.R;
import com.github.tvbox.osc.bean.DriveFolderFile;
import com.github.tvbox.osc.ui.dialog.AlistDriveDialog;
import com.github.tvbox.osc.ui.dialog.WebdavDialog;
import com.github.tvbox.osc.util.StorageDriveType;

import java.util.ArrayList;

/**
 * @author pj567
 * @date :2020/12/21
 * @description:
 */
public class DriveAdapter extends BaseQuickAdapter<DriveFolderFile, BaseViewHolder> {

    public DriveAdapter() {
        super(R.layout.item_drive, new ArrayList<>());
    }

    public void toggleDelMode(boolean isDelMode) {
        for (int pos = 0; pos < this.getItemCount(); pos++) {
            View delView = this.getViewByPosition(pos, R.id.delDrive);
            if (delView != null) delView.setVisibility(isDelMode ? View.VISIBLE : View.GONE);
            DriveFolderFile item = this.getItem(pos);
            item.isDelMode = isDelMode;
            if (item.getDriveType() == StorageDriveType.TYPE.WEBDAV || item.getDriveType() == StorageDriveType.TYPE.ALISTWEB) {
                this.getViewByPosition(pos, R.id.imgConfig).setVisibility(isDelMode ? View.GONE : View.VISIBLE);
            }
        }
    }

    @Override
    protected void convert(BaseViewHolder helper, DriveFolderFile item) {
        TextView itemName = helper.getView(R.id.txtItemName);
        if (item.name == null && item.parentFolder == item) itemName.setText(" . . ");
        else itemName.setText(item.name);
        ImageView imgItem = helper.getView(R.id.imgItem);
        TextView txtMediaName = helper.getView(R.id.txtMediaName);
        txtMediaName.setVisibility(View.GONE);
        TextView lastModified = helper.getView(R.id.txtModifiedDate);
        lastModified.setVisibility(View.GONE);
        ImageView imgConfig = helper.getView(R.id.imgConfig);
        imgConfig.setVisibility(View.GONE);
        LinearLayout mItemLayout = helper.getView(R.id.mItemLayout);
        helper.setGone(R.id.delDrive, item.isDelMode);
        mItemLayout.setOnFocusChangeListener((view, b) -> {
            txtMediaName.setSelected(b);
            mListener.onItemChildClick(this, view, getItemPosition(item));
        });
        mItemLayout.setOnClickListener(v -> mListener.onItemChildClick(this, v, getItemPosition(item)));
        if (item.isDrive()) {
            if (item.getDriveType() == StorageDriveType.TYPE.LOCAL)
                imgItem.setImageResource(R.drawable.icon_sdcard);
            else if (item.getDriveType() == StorageDriveType.TYPE.WEBDAV) {
                imgItem.setImageResource(R.drawable.icon_circle_node);
                imgConfig.setVisibility(item.isDelMode ? View.GONE : View.VISIBLE);
                imgConfig.setOnClickListener(view -> {
                    WebdavDialog dialog = new WebdavDialog(getContext(), item.getDriveData());
                    dialog.show();
                });
            } else if (item.getDriveType() == StorageDriveType.TYPE.ALISTWEB) {
                imgItem.setImageResource(R.drawable.icon_alist);
                imgConfig.setVisibility(item.isDelMode ? View.GONE : View.VISIBLE);
                imgConfig.setOnClickListener(view -> {
                    AlistDriveDialog dialog = new AlistDriveDialog(getContext(), item.getDriveData());
                    dialog.show();
                });
            }
        } else {
            lastModified.setText(item.getFormattedLastModified());
            lastModified.setVisibility(View.VISIBLE);
            if (item.isFile) {
                if (item.fileType != null) {
                    txtMediaName.setText(item.fileType);
                    txtMediaName.setVisibility(View.VISIBLE);
                }
                if (StorageDriveType.isVideoType(item.fileType))
                    imgItem.setImageResource(R.drawable.icon_film);
                else imgItem.setImageResource(R.drawable.icon_file);
            } else {
                imgItem.setImageResource(R.drawable.icon_folder);
            }
        }
    }

    private OnItemChildClickListener mListener;

    public void setDeleteListener(OnItemChildClickListener listener) {
        mListener = listener;
    }
}