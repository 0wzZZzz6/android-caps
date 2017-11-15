package com.android.curlytops.suroytabukidnon;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.lapism.searchview.SearchAdapter;
import com.lapism.searchview.SearchFilter;
import com.lapism.searchview.SearchHistoryTable;
import com.lapism.searchview.SearchItem;
import com.lapism.searchview.SearchView;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by jan_frncs
 */

public class SearchActivity extends AppCompatActivity {
    SearchView mSearchView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_layout);


        final SearchHistoryTable mHistoryDatabase = new SearchHistoryTable(this);

        mSearchView = findViewById(R.id.searchView); // from API 26
        if (mSearchView != null) {
//            mSearchView.setVersionMargins(SearchView.VersionMargins.TOOLBAR_BIG);
//            mSearchView.setTheme(SearchView.Theme.PLAY_STORE);

            mSearchView.setVersion(SearchView.Version.MENU_ITEM);
            mSearchView.setVersionMargins(SearchView.VersionMargins.MENU_ITEM);
            mSearchView.setTheme(SearchView.Theme.LIGHT);

            mSearchView.setHint(R.string.search);
            mSearchView.setNavigationIcon(R.drawable.arrow_left);
            mSearchView.setVoice(false);
            mSearchView.setFocusable(true);
            mSearchView.setNavigationIconClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
            mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    mHistoryDatabase.addItem(new SearchItem(query));
                    mSearchView.close(false);
                    Toast.makeText(SearchActivity.this, "" + query, Toast.LENGTH_SHORT).show();
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });

            List<SearchItem> suggestionsList = new ArrayList<>();
            suggestionsList.add(new SearchItem("search1"));
            suggestionsList.add(new SearchItem("search2"));
            suggestionsList.add(new SearchItem("search3"));

            SearchAdapter searchAdapter = new SearchAdapter(this, suggestionsList);
            searchAdapter.setOnSearchItemClickListener(new SearchAdapter.OnSearchItemClickListener() {
                @Override
                public void onSearchItemClick(View view, int position, String text) {
                    mHistoryDatabase.addItem(new SearchItem(text));
//                    mSearchView.close(false);
                }
            });
            mSearchView.setAdapter(searchAdapter);

            suggestionsList.add(new SearchItem("search1"));
            suggestionsList.add(new SearchItem("search2"));
            suggestionsList.add(new SearchItem("search3"));
            searchAdapter.notifyDataSetChanged();

            List<SearchFilter> filter = new ArrayList<>();
            filter.add(new SearchFilter("Filter1", true));
            filter.add(new SearchFilter("Filter2", true));
            mSearchView.setFilters(filter);
            //use mSearchView.getFiltersStates() to consider filter when performing search
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search: {
                mSearchView.open(true); // enable or disable animation
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
