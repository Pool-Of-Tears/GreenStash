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
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.rejowan.cutetoast.CuteToast
import com.starry.greenstash.R
import com.starry.greenstash.databinding.FragmentInputBinding
import com.starry.greenstash.utils.AppConstants
import com.starry.greenstash.utils.uriToBitmap
import java.text.SimpleDateFormat
import java.util.*


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
    private lateinit var viewModel: InputViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(InputViewModel::class.java)
        _binding = FragmentInputBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // listener variable of deadline result.
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }

        // deadline click listener.
        binding.inputDeadline.setOnClickListener {
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
        }

        // image picker button click listener.
        binding.imagePickerButton.setOnClickListener {
            ImagePicker.with(this)
                .compress(1024) //Final image size will be less than 1 MB
                .maxResultSize(
                    1080,
                    1080
                ) // Final image resolution will be less than 1080 x 1080
                .createIntent { intent ->
                    startForProfileImageResult.launch(intent)
                }
        }

        // save goal button click listener
        binding.inputSaveButton.setOnClickListener {
            val status = viewModel.insertItem(binding, imagePickerResult, requireContext())
            // data has been successfully validated and saved.
            if (status) {
                val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.inputSaveButton.windowToken, 0)
                findNavController().navigate(R.id.action_InputFragment_to_HomeFragment)
            }
        }
    }

    // listener variable of image picker result
    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data
            when (resultCode) {
                Activity.RESULT_OK -> {
                    //Image Uri will not be null for RESULT_OK
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateDateInView() {
        val sdf = SimpleDateFormat(AppConstants.DATE_FORMAT, Locale.US)
        binding.inputDeadline.setText(sdf.format(cal.getTime()))
    }
}