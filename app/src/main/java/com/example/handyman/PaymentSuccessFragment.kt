package com.example.handyman

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs

class PaymentSuccessFragment : Fragment() {

    private val args by navArgs<PaymentSuccessFragmentArgs>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_payment_success, container, false)

        view.findViewById<android.widget.Button>(R.id.btnReturnHome).setOnClickListener {
            val action = PaymentSuccessFragmentDirections
                .actionPaymentSuccessFragmentToCustomerJobListFragment(args.customerId)
            findNavController().navigate(action)
        }

        return view
    }
}
