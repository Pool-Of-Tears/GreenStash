package com.starry.greenstash.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.starry.greenstash.R
import com.starry.greenstash.database.Item
import com.starry.greenstash.databinding.FragmentHomeBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class HomeFragment : Fragment(), ClickListenerIF {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_HomeFragment_to_InputFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDepositClicked(item: Item) {
        TODO("Not yet implemented")
    }

    override fun onWithdrawClicked(item: Item) {
        TODO("Not yet implemented")
    }

    override fun onEditClicked(item: Item) {
        TODO("Not yet implemented")
    }

    override fun onDeleteClicked(item: Item) {
        TODO("Not yet implemented")
    }
}