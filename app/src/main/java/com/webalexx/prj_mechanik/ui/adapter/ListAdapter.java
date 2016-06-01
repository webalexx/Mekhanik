package com.webalexx.prj_mechanik.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import com.webalexx.prj_mechanik.R;
import com.webalexx.prj_mechanik.content.AppConstants;
import com.webalexx.prj_mechanik.content.model.CatalogItem;
import com.webalexx.prj_mechanik.content.model.Section;

/**
 * Adapter for {@link com.webalexx.prj_mechanik.content.model.Section}
 * http://www.perfectapk.com/android-listfragment-tutorial.html
 */
public class ListAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private final List<Section> sections = new ArrayList<>();
    private final List<CatalogItem> catalogItems = new ArrayList<>();
    private static Context context;
    private static String REST_SERVER_ROOT = AppConstants.getContext().getResources().getString(R.string.rest_server_root);

    public ListAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        this.context = context;

    }


    public void setSections(List<Section> input) {
        sections.clear();
        sections.addAll(input);
        notifyDataSetChanged();
    }

    public void setCatalogItems(List<CatalogItem> input) {
        catalogItems.clear();
        catalogItems.addAll(input);
        notifyDataSetChanged();
    }

    public boolean isSection(int position) {
        return position < sections.size();
    }

    @Override
    public int getCount() {
        return sections.size() + catalogItems.size();
    }

    @Override
    public Object getItem(int position) {
        return isSection(position)
                ? sections.get(position)
                : catalogItems.get(position - sections.size());
    }

    @Override
    public long getItemId(int position) {
        return isSection(position)
                ? sections.get(position).getId()
                : catalogItems.get(position - sections.size()).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;
        if (convertView == null) {
            view = createView(parent);
        } else {
            view = convertView;
        }

        holder = (ViewHolder) view.getTag();
        bindView(position, holder);
        return view;
    }

    private void bindView(int position, ViewHolder holder) {

        if (isSection(position)) {

            Section section = sections.get(position);
            holder.listItemSubtext.setVisibility(View.GONE);
            holder.listItemText.setText(section.getName());
            Picasso
                    .with(context)
                    .load(R.drawable.ic_folder_grey600_48dp)
                    .into(holder.listItemImage);
        } else {
            CatalogItem catalogItem = catalogItems.get(position - sections.size());
            String article = catalogItem.getArticle();
            if (article == null) {
                holder.listItemSubtext.setVisibility(View.GONE);
            } else {
                holder.listItemSubtext.setVisibility(View.VISIBLE);
                holder.listItemSubtext.setText(article);
            }
            holder.listItemText.setText(catalogItem.getName());
            String detailUri = catalogItem.getDetailUri();
            Picasso
                    .with(context)
                    .load(TextUtils.isEmpty(detailUri) ? null : REST_SERVER_ROOT + detailUri)
                    .placeholder(R.drawable.ic_camera_alt_grey600_48dp)
                    .error(R.drawable.ic_camera_alt_grey600_48dp)
                    .into(holder.listItemImage);

        }
    }

    private View createView(ViewGroup parent) {
        View view = inflater.inflate(R.layout.view_section_item, parent, false);

        ViewHolder holder = new ViewHolder(
                (TextView) view.findViewById(R.id.item_text),
                (TextView) view.findViewById(R.id.item_subtext),
                (ImageView) view.findViewById(R.id.item_image)
        );
        view.setTag(holder);

        return view;
    }


    private class ViewHolder {
        final TextView listItemText;
        final TextView listItemSubtext;
        final ImageView listItemImage;

        ViewHolder(TextView listItemText, TextView listItemSubtext, ImageView listItemImage) {
            this.listItemText = listItemText;
            this.listItemSubtext = listItemSubtext;
            this.listItemImage = listItemImage;
        }
    }
}
