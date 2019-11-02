package hu.ait.shoppinglist.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hu.ait.shoppinglist.R
import hu.ait.shoppinglist.ScrollingActivity
import hu.ait.shoppinglist.data.AppDatabase
import hu.ait.shoppinglist.data.Item
import hu.ait.shoppinglist.touch.ShoppingTouchHelperCallback
import kotlinx.android.synthetic.main.shopping_item_row.view.*
import java.util.*
import android.transition.TransitionManager
import hu.ait.shoppinglist.data.Category
import kotlinx.android.synthetic.main.activity_scrolling.*


class ShoppingListAdapter : RecyclerView.Adapter<ShoppingListAdapter.ViewHolder>, ShoppingTouchHelperCallback {

    var shoppingList = mutableListOf<Item>()

    val context: Context

    constructor(context: Context, shoppingItems: List<Item>) : super() {
        this.context = context

        shoppingList.addAll(shoppingItems)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivIcon = itemView.ivIcon
        val tvCategory = itemView.tvCategory
        val tvName = itemView.tvName
        val cbBought = itemView.cbBought
        val tvAmount = itemView.tvAmount
        val btnDelete = itemView.btnDelete
        val btnEdit = itemView.btnEdit
        val tvDetails = itemView.tvDetails
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        LayoutInflater.from(context).inflate(
            R.layout.shopping_item_row, parent, false
        )
    )

    override fun getItemCount() = shoppingList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var shoppingItem = shoppingList.get(holder.adapterPosition)

        var expanded = shoppingItem.expanded
        holder.tvDetails.visibility = if (expanded) View.VISIBLE else View.GONE

        holder.tvCategory.text = shoppingItem.category
        holder.tvName.text = shoppingItem.name
        holder.tvAmount.text =
            "${shoppingItem.amount} x $${shoppingItem.price} = $${shoppingItem.amount * shoppingItem.price}"
        holder.cbBought.isChecked = shoppingItem.purchased
        holder.tvDetails.text = shoppingItem.details

        setIcon(holder, shoppingItem.category)

        holder.btnDelete.setOnClickListener {
            removeItem(holder.adapterPosition)
        }

        holder.btnEdit.setOnClickListener {
            (context as ScrollingActivity).showEditTodoDialog(
                shoppingItem, holder.adapterPosition
            )
        }

        holder.cbBought.setOnClickListener {
            shoppingItem.purchased = holder.cbBought.isChecked

            Thread {
                AppDatabase.getInstance(context).itemDAO().updateItem(shoppingItem)
            }.start()
        }

        holder.itemView.setOnClickListener {
            var expanded = shoppingItem.expanded
            shoppingItem.expanded = !expanded
            notifyItemChanged(holder.adapterPosition)
        }
    }

    fun setIcon(holder: ViewHolder, category: String) {
        var categoryArr = category.split(" ")

        when (categoryArr[0]) {
            Category.CATEGORY_FOOD -> holder.ivIcon.setImageResource(R.drawable.food)
            Category.CATEGORY_HOUSEHOLD -> holder.ivIcon.setImageResource(R.drawable.household)
            Category.CATEGORY_CLOTHING -> holder.ivIcon.setImageResource(R.drawable.clothing)
            Category.CATEGORY_ELECTRONICS -> holder.ivIcon.setImageResource(R.drawable.electronics)
            else -> holder.ivIcon.setImageResource(R.drawable.other)
        }
    }

    fun addItem(item: Item) {
        shoppingList.add(item)

        item.position = shoppingList.lastIndex.toLong()

        Thread {
            AppDatabase.getInstance(context).itemDAO().updateItem(item)
        }.start()

        notifyItemInserted(shoppingList.lastIndex)
    }

    fun updateItem(item: Item, index: Int) {
        shoppingList.set(index, item)
        notifyItemChanged(index)
    }

    fun removeItem(index: Int) {
        Thread {
            AppDatabase.getInstance(context).itemDAO().deleteItem(shoppingList.get(index))

            (context as ScrollingActivity).runOnUiThread {
                shoppingList.removeAt(index)
                notifyItemRemoved(index)

                Thread {
                    for (i in index..shoppingList.lastIndex) {
                        shoppingList[i].position = i.toLong()
                    }
                }.start()
            }
        }.start()
    }
    fun deleteAll() {
        Thread {
            AppDatabase.getInstance(context).itemDAO().deleteAll()

            (context as ScrollingActivity).runOnUiThread {
                shoppingList.clear()
                notifyDataSetChanged()
            }
        }.start()
    }

    // TOUCH:

    override fun onDismissed(position: Int) {
        removeItem(position)
    }

    override fun onItemMoved(fromPosition: Int, toPosition: Int) {
        Collections.swap(shoppingList, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)

        shoppingList[fromPosition].position = fromPosition.toLong()
        shoppingList[toPosition].position = toPosition.toLong()

        Thread {
            AppDatabase.getInstance(context).itemDAO().updateItem(shoppingList[fromPosition])
            AppDatabase.getInstance(context).itemDAO().updateItem(shoppingList[toPosition])
        }.start()

    }
}