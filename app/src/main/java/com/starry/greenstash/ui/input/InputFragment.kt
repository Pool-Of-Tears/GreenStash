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

package com.starry.greenstash.ui.input

import android.app.Activity
import android.app.DatePickerDialog
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.rejowan.cutetoast.CuteToast
import com.starry.greenstash.R
import com.starry.greenstash.databinding.FragmentInputBinding
import com.starry.greenstash.utils.*
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class InputFragment : Fragment() {

    private var _binding: FragmentInputBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    // storing calender instance.
    private var cal = Calendar.getInstance()

    // storing image picker result
    private var imagePickerResult: Bitmap? = null

    // Input fragment's view model class.
    private val viewModel: InputViewModel by viewModels()

    // Shared view model class.
    private lateinit var sharedViewModel: SharedViewModel

    // nav options for adding animations when switching between fragments.
    private lateinit var navOptions: NavOptions

    // Progress dialog to show when compressing large images.
    private lateinit var mProgressDialog: IndeterminateProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        _binding = FragmentInputBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // build navigation options.
        val navOptionsBuilder = NavOptions.Builder()
        navOptionsBuilder.setEnterAnim(R.anim.slide_in).setExitAnim(R.anim.fade_out)
            .setPopEnterAnim(R.anim.fade_in).setPopExitAnim(R.anim.fade_out)
        navOptions = navOptionsBuilder.build()

        // build progress dialog.
        mProgressDialog = IndeterminateProgressDialog(requireContext())
        mProgressDialog.setMessage(requireContext().getString(R.string.progress_dialog_msg))
        mProgressDialog.setCanceledOnTouchOutside(false)
        mProgressDialog.setCancelable(false)

        // check if fragment was started bu clicking edit button
        // editData won't be null in that case.
        val editData = sharedViewModel.getEditData()
        if (editData != null) {
            updateInputView(editData)
        }

        // listener variable of deadline result.
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateView()
            }

        // deadline click listener.
        binding.inputDeadline.editText?.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                dateSetListener,
                // set DatePickerDialog to point to today's date when it loads up
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            )
            // sets today's date as minimum date and disable past dates.
            datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 10000
            datePickerDialog.show()
            // set positive and negative button color according to theme.
            if (isDarkModeOn(requireContext())) {
                datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE)
                    .setTextColor(Color.WHITE)
                datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE)
                    .setTextColor(Color.WHITE)
            } else {
                datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE)
                    .setTextColor(Color.BLACK)
                datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE)
                    .setTextColor(Color.BLACK)
            }
        }

        // image picker button click listener.
        binding.imagePickerButton.setOnClickListener {
            ImagePicker.with(this)
                //Final image size will be less than 1 MB
                .compress(1024)
                // Final image resolution will be less than 1080 x 1080
                .maxResultSize(1080, 1080)
                .createIntent { intent ->
                    mProgressDialog.show()
                    startForProfileImageResult.launch(intent)
                }
        }

        // save goal button click listener
        binding.inputSaveButton.setOnClickListener {
            val status = if (editData != null) {
                viewModel.insertItem(binding, imagePickerResult, requireContext(), editData)
            } else {
                viewModel.insertItem(binding, imagePickerResult, requireContext())
            }
            // data has been successfully validated and saved.
            if (status) {
                binding.inputSaveButton.dismissKeyboard()
                findNavController().navigate(
                    R.id.action_InputFragment_to_HomeFragment,
                    null, navOptions
                )
            }
        }
    }

    // listener variable of image picker result
    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data
            mProgressDialog.hide()

            when (resultCode) {
                Activity.RESULT_OK -> {
                    imagePickerResult = uriToBitmap(data!!.data!!, requireContext())
                    binding.imagePicker.setImageBitmap(imagePickerResult)
                }
                ImagePicker.RESULT_ERROR -> {
                    CuteToast.ct(
                        requireContext(),
                        ImagePicker.getError(data),
                        CuteToast.LENGTH_SHORT,
                        CuteToast.SAD, true
                    ).show()
                }
                else -> {
                    CuteToast.ct(
                        requireContext(),
                        getString(R.string.cancel),
                        CuteToast.LENGTH_SHORT,
                        CuteToast.ERROR, true
                    ).show()
                }
            }
        }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // remove search & filter icon in input screen.
        menu.removeItem(R.id.actionSearch)
        menu.removeItem(R.id.actionFilter)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateDateView() {
        val sdf = SimpleDateFormat(AppConstants.DATE_FORMAT, Locale.US)
        binding.inputDeadline.editText?.setText(sdf.format(cal.time))
    }

    private fun updateInputView(itemData: ItemEditData) {
        if (itemData.image != null) {
            binding.imagePicker.setImageBitmap(itemData.image)
            imagePickerResult = itemData.image
        }
        binding.inputTitle.editText?.setText(itemData.title)
        binding.inputAmount.editText?.setText(itemData.amount)
        binding.inputDeadline.editText?.setText(itemData.date)
        binding.inputSaveButton.text = requireContext().getString(R.string.input_edit_save_button)
    }
}