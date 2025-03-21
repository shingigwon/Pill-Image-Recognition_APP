package com.example.camera_app.Service;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.camera_app.R;

import java.util.ArrayList;


public class ListViewAdapterDetail extends BaseAdapter {

    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<ListViewItem> listViewItemList = new ArrayList<ListViewItem>();

    // ListViewAdapter의 생성자
    public ListViewAdapterDetail() {

    }

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return listViewItemList.size() ;
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.show_detail_item, parent, false);
        }

        //
        ImageView PillImageView = (ImageView) convertView.findViewById(R.id.testimage) ;


        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        TextView ItemNameTextView = (TextView) convertView.findViewById(R.id.item_name) ;
        TextView CompanyTextView = (TextView) convertView.findViewById(R.id.company) ;
        TextView CF_NameTextView = (TextView) convertView.findViewById(R.id.classification) ;
        TextView UDTextView = (TextView) convertView.findViewById(R.id.UD) ;
        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        ListViewItem listViewItem = listViewItemList.get(position);


        //
        PillImageView.setImageBitmap(listViewItem.getImage());

        // 아이템 내 각 위젯에 데이터 반영
        ItemNameTextView.setText(listViewItem.getItem_Name());
        CompanyTextView.setText(listViewItem.getCompany());
        CF_NameTextView.setText(listViewItem.getClassification());
        UDTextView.setText(listViewItem.getUD());

        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position ;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position) ;
    }


    public Bitmap getPosImage(int position) {
        return listViewItemList.get(position).getImage() ;
    }

    public String getPosItemName(int position) {
        return listViewItemList.get(position).getItem_Name() ;
    }

    public String getPosCompoany(int position) {
        return listViewItemList.get(position).getCompany() ;
    }

    public String getPosClassification_Name(int position) {
        return listViewItemList.get(position).getClassification() ;
    }

    public String getPosUD(int position) {
        return listViewItemList.get(position).getUD() ;
    }


    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
//    public void addItem(String itemname, String company, String classification_name, String ud) {
//        ListViewItem item = new ListViewItem();
//
//        item.setItem_Name(itemname);
//        item.setCompany(company);
//        item.setClassification(classification);
//        item.setUd(ud);
//
//        listViewItemList.add(item);
//    }

    public void addItem(Bitmap image, String itemname, String company, String classification, String ud) {
        ListViewItem item = new ListViewItem();
        item.setImage(image);
        item.setItem_Name(itemname);
        item.setCompany(company);
        item.setClassification(classification);
        item.setUD(ud);
        listViewItemList.add(item);
    }
}
