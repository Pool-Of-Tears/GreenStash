package com.starry.greenstash.ui.input

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
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
import java.text.SimpleDateFormat
import java.util.*


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class InputFragment : Fragment() {

    private var _binding: FragmentInputBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    // storing calender instance.
    private var cal = Calendar.getInstance()!!

    // storing image picker result
    private var imagePickerResult: Intent? = null

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

        // setup date listener variable.
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }

        binding.inputDeadline!!.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                dateSetListener,
                // set DatePickerDialog to point to today's date when it loads up
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

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

        binding.inputSaveButton.setOnClickListener {
            val status = viewModel.insertItem(binding, imagePickerResult, requireContext())
            // data has been successfully validated and saved.
            if (status == true) {
                val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.inputSaveButton.windowToken, 0)
                findNavController().navigate(R.id.action_InputFragment_to_HomeFragment)
            }
        }
    }

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data
            when (resultCode) {
                Activity.RESULT_OK -> {
                    //Image Uri will not be null for RESULT_OK
                    imagePickerResult = data!!
                    binding.imagePicker.setImageURI(data.data)
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
        val myFormat = "dd/MM/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        binding.inputDeadline.setText(sdf.format(cal.getTime()))
    }
}