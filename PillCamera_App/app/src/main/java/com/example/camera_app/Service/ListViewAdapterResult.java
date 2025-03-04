package com.example.camera_app.Service;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.camera_app.R;

import java.util.ArrayList;


public class ListViewAdapterResult extends BaseAdapter {

    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<ListViewItem> listViewItemList = new ArrayList<ListViewItem>();

    // ListViewAdapter의 생성자
    public ListViewAdapterResult() {

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
            convertView = inflater.inflate(R.layout.show_result_item, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        ImageView PillImageView = (ImageView) convertView.findViewById(R.id.pill_img) ;
        TextView PillNameTextView = (TextView) convertView.findViewById(R.id.pill_name) ;

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        final ListViewItem listViewItem = listViewItemList.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        PillImageView.setImageBitmap(listViewItem.getImage());
        PillNameTextView.setText(listViewItem.getItem_Name());

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

    public Bitmap getPosImage(int position){
        return listViewItemList.get(position).getImage();
    }

    public String getPosItem_Name(int position){
        return listViewItemList.get(position).getItem_Name();
    }
    public String getPosCompony(int position) {
        return listViewItemList.get(position).getCompany() ;
    }

    public String getPosClassification_Name(int position) {
        return listViewItemList.get(position).getClassification() ;
    }

    public String getPosEE(int position) {
        return listViewItemList.get(position).getEE() ;
    }
    public String getPosUD(int position) {
        return listViewItemList.get(position).getUD() ;
    }
    public String getPosNB(int position) {
        return listViewItemList.get(position).getNB() ;
    }



    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(Bitmap image, String item_name, String company, String cn, String EE, String UD, String NB) {
        ListViewItem item = new ListViewItem();

        item.setImage(image);
        item.setItem_Name(item_name);
        item.setCompany(company);
        item.setClassification(cn);
        item.setEE(EE);
        item.setUD(UD);
        item.setNB(NB);

        listViewItemList.add(item);
    }


}
