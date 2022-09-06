/*
MIT License

Copyright (c) 2022 Stɑrry Shivɑm

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

package com.starry.greenstash.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.starry.greenstash.databinding.FragmentInfoBinding
import com.starry.greenstash.ui.adapters.InfoRVAdapter
import com.starry.greenstash.ui.viewmodels.SharedViewModel
import com.starry.greenstash.utils.formatCurrency
import com.starry.greenstash.utils.gone


class InfoFragment : Fragment() {

    // Shared view model class.
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var binding: FragmentInfoBinding
    private lateinit var settingPerf: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        settingPerf = PreferenceManager.getDefaultSharedPreferences(requireContext())
        binding = FragmentInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val defCurrency = settingPerf.getString("currency", "")
        val infoItem = sharedViewModel.getInfoItem()
        val progress = ((infoItem.currentAmount / infoItem.totalAmount) * 100).toInt()

        binding.infoProgressBar.setProgress(progress, true)
        binding.infoTitle.text = infoItem.title
        binding.infoEndDate.text = infoItem.deadline
        binding.infoTotalAmount.text = "$defCurrency${formatCurrency(infoItem.totalAmount)}"
        binding.infoCurrentAmount.text = "$defCurrency${formatCurrency(infoItem.currentAmount)}"
        val remainingAmount = infoItem.totalAmount - infoItem.currentAmount
        binding.infoRemainingAmount.text = "$defCurrency${formatCurrency(remainingAmount)}"

        if (infoItem.transactions == null) {
            binding.transactionView.gone()
        } else {
            val adapter = InfoRVAdapter(requireContext(), infoItem.transactions)
            binding.transactionHistoryLV.adapter = adapter
        }

    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.clear()
    }


}