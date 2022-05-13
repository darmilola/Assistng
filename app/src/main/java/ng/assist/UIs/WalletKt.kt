package ng.assist.UIs


import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import ng.assist.*
import ng.assist.Adapters.WalletAdapter
import ng.assist.UIs.ViewModel.TransactionDatabase
import ng.assist.UIs.ViewModel.Transactions
import ng.assist.UIs.ViewModel.WalletTransactionsModel
import java.text.DecimalFormat

/**
 * A simple [Fragment] subclass.
 */
class WalletKt : Fragment() {


    internal lateinit var walletRecyclerview: RecyclerView
    internal var walletHistoryList = ArrayList<WalletTransactionsModel>()
    internal lateinit var adapter: WalletAdapter
    internal lateinit var view: View
    internal lateinit var collapsingToolbarLayout: CollapsingToolbarLayout
    internal lateinit var toolbar: Toolbar
    internal lateinit var walletAppbar: AppBarLayout
    internal lateinit var toolbarTitle: TextView
    internal lateinit var walletTransfer: LinearLayout
    internal lateinit var topUp: LinearLayout
    internal lateinit var withdrawals: LinearLayout
    internal lateinit var bills: LinearLayout
    internal lateinit var walletBalanceText: TextView
    internal lateinit var pendingImage: ImageView
    internal var mWalletBalance: String? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_wallet, container, false)

        initView()
        return view
    }

    private fun initView() {

        val preferences = PreferenceManager.getDefaultSharedPreferences(
            requireContext()
        )
        pendingImage = view.findViewById(R.id.pending_badge)
        walletBalanceText = view.findViewById(R.id.wallet_balance_text)
        walletRecyclerview = view.findViewById(R.id.wallet_transcations_recyclerview)
        collapsingToolbarLayout = view.findViewById(R.id.wallet_collapsing_toolbar_layout)
        toolbar = view.findViewById(R.id.wallet_toolbar)
        walletAppbar = view.findViewById(R.id.wallet_app_bar)
        toolbarTitle = view.findViewById(R.id.wallet_toolbar_title)
        walletTransfer = view.findViewById(R.id.wallet_transfer_money)
        topUp = view.findViewById(R.id.top_up_layout)
        withdrawals = view.findViewById(R.id.withdrawals)
        bills = view.findViewById(R.id.bills)
        mWalletBalance = requireArguments().getString("walletBalance")
        val amount = java.lang.Double.parseDouble(mWalletBalance!!)
        val formatter = DecimalFormat("#,###.00")
        val formatted = formatter.format(amount)
        walletBalanceText.text = "NGN $formatted"
        initTransactions();
        topUp.setOnClickListener { startActivityForResult(Intent(context, TopUp::class.java), TOP_UP_REQ) }

        walletTransfer.setOnClickListener {
            startActivityForResult(
                    Intent(context, SendMoney::class.java),
                    SEND_MONEY_REQ
                )

        }

        bills.setOnClickListener { startActivity(Intent(context, Bills::class.java)) }
        withdrawals.setOnClickListener {

            val isVerified = preferences.getString("isVerified", "false")

            if (isVerified.equals("true", ignoreCase = true)) {
                startActivity(Intent(context, RequestWithdrawal::class.java))
            } else {
                Toast.makeText(
                    context,
                    "You Need to be verified to make withdrawals",
                    Toast.LENGTH_LONG
                ).show()
            }


        }


        if(isPendingAvailable()){
            pendingImage.visibility = View.VISIBLE
        }
        else{
            pendingImage.visibility = View.GONE
        }


        walletAppbar.addOnOffsetChangedListener(object :  AppBarLayout.OnOffsetChangedListener {

             var isShown = true
             var scrollRange = -1
            override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.totalScrollRange
                }
                if (scrollRange + verticalOffset == 0) {

                    toolbarTitle.text = "Transactions"
                    isShown = true
                    return
                } else if (isShown) {
                    isShown = false
                    toolbarTitle.text = ""
                    return
                }
            }
        })
    }

    override fun onResume() {

        updateWalletBalanceFromSharedPref(requireContext())
        initTransactions()

        if(isPendingAvailable()){
            pendingImage.visibility = View.VISIBLE
        }
        else{
            pendingImage.visibility = View.GONE
        }

        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requireActivity().window.navigationBarColor = ContextCompat.getColor(requireContext(), R.color.special_activity_background)
            requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.special_activity_background)
            // getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS );
            requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            // getWindow().setFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS,WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == TOP_UP_REQ && resultCode == 0 && data != null) {
            val balance = data.getStringExtra("balance")
            val amount = java.lang.Double.parseDouble(balance!!)
            val formatter = DecimalFormat("#,###.00")
            val formatted = formatter.format(amount)
            walletBalanceText.text = "NGN $formatted"
        }
        if (requestCode == SEND_MONEY_REQ && resultCode == 0 && data != null) {
            ///  String amountSent = data.getStringExtra("amountSent");
            // int amountSentInt = Integer.parseInt(amountSent);

            //DecimalFormat formatter = new DecimalFormat("#,###.00");
            //String formatted = formatter.format(trueBalance);
            //walletBalanceText.setText("NGN "+formatted);

        }
    }




    fun initTransactions(){
        val db = TransactionDatabase.getTransactionDatabase(context = requireActivity().applicationContext!!)
        val transactionDao = db!!.transactionDao();
        val transactions: List<Transactions> = transactionDao.getAll()

        adapter = WalletAdapter(ArrayList(transactions), context)
        walletRecyclerview.adapter = adapter
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)
        walletRecyclerview.layoutManager = layoutManager
        walletRecyclerview.adapter = adapter
    }

    companion object {
        private val TOP_UP_REQ = 0
        private val SEND_MONEY_REQ = 3
    }

    private fun isPendingAvailable(): Boolean {
        val preferences =
            PreferenceManager.getDefaultSharedPreferences(requireContext())
        return preferences.getBoolean("isPending", false)
    }

    private fun updateWalletBalanceFromSharedPref(context: Context) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val walletBalance = preferences.getString("walletBalance", "0")
        val amount = walletBalance!!.toDouble()
        val formatter = DecimalFormat("#,###.00")
        val formatted = formatter.format(amount)
        walletBalanceText.text = "NGN $formatted"
    }


}// Required empty public constructor
