/*
MIT License

Copyright (c) 2022 Stɑrry Shivɑm // This file is part of GreenStash.

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */

package com.starry.greenstash.ui.home

import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.rejowan.cutetoast.CuteToast
import com.starry.greenstash.R
import com.starry.greenstash.database.Item
import com.starry.greenstash.databinding.FragmentHomeBinding
import com.starry.greenstash.utils.ItemEditData
import com.starry.greenstash.utils.SharedViewModel
import com.starry.greenstash.utils.gone
import com.starry.greenstash.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


@AndroidEntryPoint
class HomeFragment : Fragment(), ClickListenerIF {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    // home fragments view model.
    private val viewModel: HomeViewModel by viewModels()

    // Shared view model class.
    private lateinit var sharedViewModel: SharedViewModel

    // home recycle view adapter.
    private lateinit var adapter: HomeRVAdapter

    // nav options for adding animations when switching between fragments.
    private lateinit var navOptions: NavOptions

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // build navigation options.
        val navOptionsBuilder = NavOptions.Builder()
        navOptionsBuilder.setEnterAnim(R.anim.slide_in).setExitAnim(R.anim.fade_out)
            .setPopEnterAnim(R.anim.fade_in).setPopExitAnim(R.anim.fade_out)
        navOptions = navOptionsBuilder.build()
        // set click listener on add goal fab button.
        binding.fab.setOnClickListener {
            findNavController().navigate(
                R.id.action_HomeFragment_to_InputFragment,
                null, navOptions
            )
        }
        // attach adapter to recycler view.
        adapter = HomeRVAdapter(requireContext(), this)
        binding.mainRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.mainRecyclerView.adapter = adapter
        // observe changes in items array and update homepage accordingly.
        viewModel.allItems.observe(viewLifecycleOwner) { itemList ->
            itemList.let {
                adapter.updateItemsList(it)
                checkDataset()
            }
        }
        // hide fab button on scrolling.
        binding.mainRecyclerView.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    if (dy > 0) {
                        // Scroll Down
                        if (binding.fab.isShown) {
                            binding.fab.hide()
                        }
                    } else if (dy < 0) {
                        // Scroll Up
                        if (!binding.fab.isShown) {
                            binding.fab.show()
                        }
                    }
                }
            }
        )
    }

    override fun onDepositClicked(item: Item) {
        if (item.currentAmount >= item.totalAmount) {
            CuteToast.ct(
                requireContext(),
                requireContext().getString(R.string.goal_already_achieved),
                CuteToast.LENGTH_SHORT,
                CuteToast.SUCCESS, true
            ).show()
        } else {
            val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dw_dialog, null)
            val amountEditText = dialogView.findViewById<EditText>(R.id.alertDialogDW)
            // build alert dialog.
            val alertDialog = MaterialAlertDialogBuilder(requireContext())
            alertDialog.setTitle(requireContext().getString(R.string.deposit_dialog_title))
            alertDialog.setView(dialogView)
            alertDialog.setCancelable(false)
            // set negative button.
            alertDialog.setNegativeButton("Cancel") { _, _ ->
            }
            // set positive button.
            alertDialog.setPositiveButton("Done") { _, _ ->
                if (!(amountEditText.text.isBlank() || amountEditText.text.isEmpty())) {
                    val newAmount = amountEditText.text.toString().toFloat()
                    viewModel.deposit(newAmount, item, requireContext())
                } else {
                    CuteToast.ct(
                        requireContext(),
                        requireContext().getString(R.string.amount_empty_err),
                        CuteToast.LENGTH_SHORT,
                        CuteToast.SAD, true
                    ).show()
                }
            }
            alertDialog.create().show()
        }

    }

    override fun onWithdrawClicked(item: Item) {
        if (item.currentAmount == 0f) {
            CuteToast.ct(
                requireContext(),
                requireContext().getString(R.string.withdraw_btn_error),
                CuteToast.LENGTH_SHORT,
                CuteToast.SAD, true
            ).show()
        } else {
            val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dw_dialog, null)
            val amountEditText = dialogView.findViewById<EditText>(R.id.alertDialogDW)
            // build alert dialog.
            val alertDialog = MaterialAlertDialogBuilder(requireContext())
            alertDialog.setTitle(requireContext().getString(R.string.withdraw_dialog_title))
            alertDialog.setView(dialogView)
            alertDialog.setCancelable(false)
            // set negative button.
            alertDialog.setNegativeButton("Cancel") { _, _ ->
            }
            // set positive button.
            alertDialog.setPositiveButton("Done") { _, _ ->
                if (!(amountEditText.text.isBlank() || amountEditText.text.isEmpty())) {
                    val newAmount = amountEditText.text.toString().toFloat()
                    viewModel.withdraw(newAmount, item, requireContext())

                } else {
                    CuteToast.ct(
                        requireContext(),
                        requireContext().getString(R.string.amount_empty_err),
                        CuteToast.LENGTH_SHORT,
                        CuteToast.SAD, true
                    ).show()
                }
            }
            alertDialog.create().show()
        }
    }

    override fun onInfoClicked(item: Item) {
        sharedViewModel.setInfoItem(item)
        findNavController().navigate(
            R.id.action_HomeFragment_to_InfoFragment,
            null, navOptions
        )
    }


    override fun onEditClicked(item: Item) {
        val editData = ItemEditData(
            item.id,
            item.title,
            item.totalAmount.toInt().toString(),
            item.deadline,
            item.itemImage
        )
        sharedViewModel.setEditData(editData)
        findNavController().navigate(
            R.id.action_HomeFragment_to_InputFragment,
            null, navOptions
        )
    }

    override fun onDeleteClicked(item: Item) {
        val alertDialog = MaterialAlertDialogBuilder(requireContext())
        alertDialog.setTitle(requireContext().getString(R.string.goal_delete_confirmation))
        alertDialog.setCancelable(false)
        // set negative button.
        alertDialog.setNegativeButton("Cancel") { _, _ ->
        }
        alertDialog.setPositiveButton("Yes") { _, _ ->
            viewModel.deleteItem(item)
            CuteToast.ct(
                requireContext(),
                requireContext().getString(R.string.goal_delete_success),
                CuteToast.LENGTH_SHORT,
                CuteToast.SUCCESS, true
            )
        }
        alertDialog.create().show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        val searchMenu = menu.findItem(R.id.actionSearch).actionView
        (searchMenu as SearchView).setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchItem(newText!!)
                return false
            }
        })

        val filterMenu = menu.findItem(R.id.actionFilter)
        filterMenu.setOnMenuItemClickListener {
            showFilterDialog(); true
        }
    }

    private fun showFilterDialog() {
        val itemList: List<Item> = viewModel.allItems.value!!
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(R.layout.filter_menu)

        val filterAll = bottomSheetDialog.findViewById<LinearLayout>(R.id.filterAll)
        val filterOngoing = bottomSheetDialog.findViewById<LinearLayout>(R.id.filterOngoing)
        val filterCompleted = bottomSheetDialog.findViewById<LinearLayout>(R.id.filterCompleted)

        filterAll!!.setOnClickListener {
            adapter.updateItemsList(itemList)
            Toast.makeText(requireContext(), "Showing all goals.", Toast.LENGTH_SHORT).show()
            bottomSheetDialog.hide()
        }
        filterOngoing!!.setOnClickListener {
            adapter.updateItemsList(itemList.filter { it.currentAmount < it.totalAmount })
            Toast.makeText(requireContext(), "Showing ongoing goals.", Toast.LENGTH_SHORT).show()
            bottomSheetDialog.hide()
        }
        filterCompleted!!.setOnClickListener {
            adapter.updateItemsList(itemList.filter { it.currentAmount >= it.totalAmount })
            Toast.makeText(requireContext(), "Showing completed goals.", Toast.LENGTH_SHORT).show()
            bottomSheetDialog.hide()
        }
        bottomSheetDialog.show()
    }

    private fun searchItem(text: String) {
        // create a new array list to filter goals.
        val filteredList: ArrayList<Item> = ArrayList()

        // running a for loop to compare elements.
        for (item in viewModel.allItems.value!!) {
            // check if the entered string matched with any item in recycler view.
            if (item.title.lowercase(Locale.getDefault())
                    .contains(text.lowercase(Locale.getDefault()))
            ) {
                filteredList.add(item)
            }
        }
        if (viewModel.allItems.value!!.isNotEmpty() && filteredList.isEmpty()) {
            CuteToast.ct(
                requireContext(),
                requireContext().getString(R.string.item_not_found),
                CuteToast.LENGTH_SHORT,
                CuteToast.SAD, true
            ).show()
        } else {
            adapter.updateItemsList(filteredList)
        }
    }


    private fun checkDataset() {
        if (viewModel.allItems.value?.isEmpty() == true) {
            binding.mainRecyclerView.gone()
            binding.homeEmptyView.visible()
        } else {
            binding.homeEmptyView.gone()
            binding.mainRecyclerView.visible()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}