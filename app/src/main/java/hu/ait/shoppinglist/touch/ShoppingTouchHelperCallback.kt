package hu.ait.shoppinglist.touch

// implement in ADAPTER class
interface ShoppingTouchHelperCallback {
    fun onDismissed(position: Int)
    fun onItemMoved(fromPosition: Int, toPosition: Int)
}