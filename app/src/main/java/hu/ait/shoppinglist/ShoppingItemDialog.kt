package hu.ait.shoppinglist

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import hu.ait.shoppinglist.data.Item
import kotlinx.android.synthetic.main.shopping_item_dialog.view.*
import java.lang.RuntimeException
import java.util.*

class ShoppingItemDialog: DialogFragment() {

    interface ShoppingItemHandler {
        fun itemCreated(item: Item)

        fun itemUpdated(item: Item)
    }

    lateinit var shoppingItemHandler: ShoppingItemHandler

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context is ShoppingItemHandler) {
            shoppingItemHandler = context
        } else {
            throw RuntimeException("The Activity is not implementing the ShoppingItemHandler interface")
        }
    }

    lateinit var etName: EditText
    lateinit var etAmount: EditText
    lateinit var etPrice: EditText
    lateinit var etDetails: EditText
    lateinit var spinnerCategory: Spinner

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogBuilder = AlertDialog.Builder(requireContext())

        dialogBuilder.setTitle(getString(R.string.dialog_add_item_title))
        val dialogView = requireActivity().layoutInflater.inflate(
            R.layout.shopping_item_dialog, null
        )

        etName = dialogView.etName
        etAmount = dialogView.etAmount
        etPrice = dialogView.etPrice
        etDetails = dialogView.etDetails
        spinnerCategory = dialogView.spinnerCategory

        val categoryAdapter = ArrayAdapter.createFromResource(
            activity as Context,
            R.array.categories,
            android.R.layout.simple_spinner_item
        )
        categoryAdapter.setDropDownViewResource(
            android.R.layout.simple_dropdown_item_1line
        )
        spinnerCategory.adapter = categoryAdapter

        dialogBuilder.setView(dialogView)

        // IF WE ARE IN EDIT MODE
        enterEditMode(dialogBuilder)

        dialogBuilder.setPositiveButton("Ok") {
                dialog, which ->

        }

        dialogBuilder.setNegativeButton("Cancel") {
                dialog, which ->
        }

        return dialogBuilder.create()
    }

    private fun enterEditMode(dialogBuilder: AlertDialog.Builder) {
        if (arguments != null && arguments!!.containsKey(ScrollingActivity.KEY_TODO_EDIT)) {
            val shoppingItem = arguments!!.getSerializable(ScrollingActivity.KEY_TODO_EDIT) as Item

            etName.setText(shoppingItem.name)
            etAmount.setText(shoppingItem.amount.toString())
            etPrice.setText(shoppingItem.price.toString())
            etDetails.setText(shoppingItem.details)

            when (shoppingItem.category) {
                "FOOD - Beverages" -> spinnerCategory.setSelection(1)
                "FOOD - Canned Goods" -> spinnerCategory.setSelection(2)
                "FOOD - Dairy" -> spinnerCategory.setSelection(3)
                "FOOD - Dry/Baking Goods" -> spinnerCategory.setSelection(4)
                "FOOD - Frozen Foods" -> spinnerCategory.setSelection(5)
                "FOOD - Meat" -> spinnerCategory.setSelection(6)
                "FOOD - Vegetables/Fruits" -> spinnerCategory.setSelection(7)
                "HOUSEHOLD - Cleaners" -> spinnerCategory.setSelection(8)
                "HOUSEHOLD - Paper Goods" -> spinnerCategory.setSelection(9)
                "HOUSEHOLD - Personal Care" -> spinnerCategory.setSelection(10)
                "ELECTRONICS" -> spinnerCategory.setSelection(11)
                "CLOTHING" -> spinnerCategory.setSelection(12)
                "OTHER" -> spinnerCategory.setSelection(13)
                else -> spinnerCategory.setSelection(0)
            }

            dialogBuilder.setTitle(getString(R.string.dialog_edit_item_title))
        }
    }

    override fun onResume() {
        super.onResume()

        val positiveButton = (dialog as AlertDialog).getButton(Dialog.BUTTON_POSITIVE)
        positiveButton.setOnClickListener {

            when {
                checkConditionDialog() -> {

                    if (arguments != null && arguments!!.containsKey(ScrollingActivity.KEY_TODO_EDIT)) {
                        updateItem()
                    } else {
                        createNewItem()
                    }

                    dialog.dismiss()
                }
            }
        }
    }

    private fun updateItem() {
        val itemToEdit = arguments!!.getSerializable(ScrollingActivity.KEY_TODO_EDIT) as Item

        itemToEdit.name = etName.text.toString()
        itemToEdit.amount = etAmount.text.toString().toLong()
        itemToEdit.price = etPrice.text.toString().toDouble()
        itemToEdit.details = etDetails.text.toString()
        itemToEdit.category = spinnerCategory.selectedItem.toString()

        shoppingItemHandler.itemUpdated(itemToEdit)
    }

    private fun createNewItem() {
        shoppingItemHandler.itemCreated(
            Item(
                null,
                spinnerCategory.selectedItem.toString(),
                etName.text.toString(),
                etAmount.text.toString().toLong(),
                etPrice.text.toString().toDouble(),
                etDetails.text.toString(),
                0.toLong(),
                purchased = false,
                expanded = false
            )
        )
    }


    fun checkConditionDialog() : Boolean =
        etName.text.isNotEmpty() &&
                etAmount.text.isNotEmpty() &&
                spinnerCategory.selectedItem.toString().isNotEmpty()

}
