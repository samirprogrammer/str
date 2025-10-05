package arena.kids.stories.activities;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.SearchView;
import android.widget.Toast;

import arena.kids.stories.R;
public class SearchResult extends Activity implements SearchView.OnQueryTextListener   {

    private SearchView searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("searc","searc");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_res);
        handleIntent(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //Toast msg = Toast.makeText(arena.kids.stories.activities.SearchResult.this, "rest"+query, Toast.LENGTH_LONG);
            //msg.show();
            //use the query to search
        }
    }
    @Override
    public boolean onQueryTextSubmit(String query) {
        //Toast msg = Toast.makeText(arena.kids.stories.activities.SearchResult.this, "rest"+query, Toast.LENGTH_LONG);
        //msg.show();
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onQueryTextChange(String newText) {
        //friendListAdapter.getFilter().filter(newText);

        // use to enable search view popup text
//        if (TextUtils.isEmpty(newText)) {
//            friendListView.clearTextFilter();
//        }
//        else {
//            friendListView.setFilterText(newText.toString());
//        }
        //Toast msg = Toast.makeText(arena.kids.stories.activities.SearchResult.this, newText, Toast.LENGTH_LONG);
        //msg.show();
        return true;
    }
}
