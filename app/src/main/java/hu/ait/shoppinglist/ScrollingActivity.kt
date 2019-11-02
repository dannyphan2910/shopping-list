package hu.ait.shoppinglist

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import hu.ait.shoppinglist.adapter.ShoppingListAdapter
import hu.ait.shoppinglist.data.AppDatabase
import hu.ait.shoppinglist.data.Category
import hu.ait.shoppinglist.data.Item
import hu.ait.shoppinglist.touch.ShoppingRecyclerTouchCallback
import kotlinx.android.synthetic.main.activity_scrolling.*
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt

class ScrollingActivity : AppCompatActivity(), ShoppingItemDialog.ShoppingItemHandler {

    companion object {
        val KEY_TODO_EDIT = "KEY_TODO_EDIT"
        val NAME_PREF = "NAME_PREF"
        val KEY_STARTED = "KEY_STARTED"
    }

    lateinit var shoppingListAdapter: ShoppingListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrolling)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            ShoppingItemDialog().show(supportFragmentManager, "Dialog")
        }

        btnDeleteAll.setOnClickListener {
            shoppingListAdapter.deleteAll()
        }

        initRecyclerView()


        if (!getWasStarted()) {
            MaterialTapTargetPrompt.Builder(this)
                .setTarget(R.id.fab)
                .setPrimaryText(getString(R.string.create_prompt_primary))
                .setSecondaryText(getString(R.string.create_prompt_secondary))
                .show()

            saveWasStarted()
        }
    }

    fun saveWasStarted() {
        var sharedPref = getSharedPreferences(NAME_PREF, Context.MODE_PRIVATE)
        var editor = sharedPref.edit()
        editor.putBoolean(KEY_STARTED, true)
        editor.apply()
    }

    fun getWasStarted() = getSharedPreferences(NAME_PREF, Context.MODE_PRIVATE).getBoolean(KEY_STARTED, false)

    private fun initRecyclerView() {
        Thread {

            var items = AppDatabase.getInstance(this@ScrollingActivity).itemDAO().getAllItems()

            runOnUiThread {
                // connects the ADAPTER with the RECYCLERVIEW
                shoppingListAdapter = ShoppingListAdapter(this, items)
                recyclerItem.adapter = shoppingListAdapter

                // connects ItemTouchHelper with RecyclerView
                val touchCallbackList = ShoppingRecyclerTouchCallback(shoppingListAdapter)
                val itemTouchHelper = ItemTouchHelper(touchCallbackList)
                itemTouchHelper.attachToRecyclerView(recyclerItem)
            }

        }.start()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_scrolling, menu)
        return true
    }

    @SuppressLint("RestrictedApi")
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.category_home -> {
                filterMode(Category.CATEGORY_HOME)
                fab.visibility = View.VISIBLE
                btnDeleteAll.visibility = View.VISIBLE
            }
            R.id.category_not_purchased -> {
                filterMode(Category.CATEGORY_NOT_PURCHASED)
                disableBtn()
            }
            R.id.category_purchased -> {
                filterMode(Category.CATEGORY_PURCHASED)
                disableBtn()
            }
            R.id.category_food -> {
                filterMode(Category.CATEGORY_FOOD)
                disableBtn()
            }
            R.id.category_clothing -> {
                filterMode(Category.CATEGORY_CLOTHING)
                disableBtn()
            }
            R.id.category_household -> {
                filterMode(Category.CATEGORY_HOUSEHOLD)
                disableBtn()
            }
            R.id.category_electronics -> {
                filterMode(Category.CATEGORY_ELECTRONICS)
                disableBtn()
            }
            R.id.category_other -> {
                filterMode(Category.CATEGORY_OTHER)
                disableBtn()
            }
        }

        return true
    }

    @SuppressLint("RestrictedApi")
    fun disableBtn() {
        fab.visibility = View.INVISIBLE
        btnDeleteAll.visibility = View.INVISIBLE
    }

    fun filterMode(byCategory: String) {
        Thread {
            var databaseDAO = AppDatabase.getInstance(this@ScrollingActivity).itemDAO()
            var data =
                when (byCategory) {
                    Category.CATEGORY_HOME -> databaseDAO.getAllItems().toMutableList()
                    Category.CATEGORY_NOT_PURCHASED -> databaseDAO.filterByNotPurchased().toMutableList()
                    Category.CATEGORY_PURCHASED -> databaseDAO.filterByPurchased().toMutableList()
                    Category.CATEGORY_FOOD -> databaseDAO.filterByFood().toMutableList()
                    Category.CATEGORY_HOUSEHOLD -> databaseDAO.filterByHousehold().toMutableList()
                    Category.CATEGORY_CLOTHING -> databaseDAO.filterByClothing().toMutableList()
                    Category.CATEGORY_ELECTRONICS -> databaseDAO.filterByElectronics().toMutableList()
                    else -> databaseDAO.filterByOther().toMutableList()
                }

            runOnUiThread {
                shoppingListAdapter.shoppingList.clear()
                shoppingListAdapter = ShoppingListAdapter(this, data)
                recyclerItem.adapter = shoppingListAdapter
                Toast.makeText(this, getString(R.string.toast_category, byCategory), Toast.LENGTH_LONG).show()
            }

        }.start()
    }

    // FOR DIALOG:

    var editIndex: Int = -1

    fun showEditTodoDialog(itemToEdit: Item, itemIndex: Int) {
        editIndex = itemIndex

        val editTodoDialog = ShoppingItemDialog()

        val bundle = Bundle()
        bundle.putSerializable(KEY_TODO_EDIT, itemToEdit)
        editTodoDialog.arguments = bundle

        editTodoDialog.show(supportFragmentManager, "EDITDIALOG")
    }

    override fun itemCreated(item: Item) {
        Thread {
            var newId = AppDatabase.getInstance(this@ScrollingActivity).itemDAO().insertItem(item)
            item.id = newId

            runOnUiThread {
                shoppingListAdapter.addItem(item)
            }

        }.start()
    }

    override fun itemUpdated(item: Item) {
        Thread {
            AppDatabase.getInstance(this@ScrollingActivity).itemDAO().updateItem(item)

            runOnUiThread {
                shoppingListAdapter.updateItem(item, editIndex)
            }
        }.start()
    }
}
