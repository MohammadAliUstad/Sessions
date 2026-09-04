package com.yugentech.sessions.utils

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryProductDetailsResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class BillingManager(context: Context) {

    private val _purchaseEvent = MutableSharedFlow<String>()
    val purchaseEvent = _purchaseEvent.asSharedFlow()

    private var productDetailsCache: List<ProductDetails> = emptyList()

    private val productIds = listOf("donation_coffee", "donation_lunch")

    private val purchasesUpdatedListener = PurchasesUpdatedListener { result, purchases ->
        when (result.responseCode) {
            BillingClient.BillingResponseCode.OK if purchases != null -> {
                for (purchase in purchases) {
                    handlePurchase(purchase)
                }
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> {
                Timber.i("User canceled the purchase")
            }
            else -> {
                val errorMessage = "Billing error: ${result.debugMessage.ifBlank { "Unknown Error" }}"
                Timber.e(errorMessage)
                CoroutineScope(Dispatchers.Main).launch {
                    _purchaseEvent.emit(errorMessage)
                }
            }
        }
    }

    private val billingClient = BillingClient.newBuilder(context)
        .setListener(purchasesUpdatedListener)
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder().enableOneTimeProducts().build()
        )
        .build()

    fun startConnection() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    Timber.d("Billing Setup Done")
                    queryProducts()
                } else {
                    CoroutineScope(Dispatchers.Main).launch {
                        _purchaseEvent.emit("Billing Setup Failed")
                    }
                }
            }
            override fun onBillingServiceDisconnected() {
                Timber.w("Billing Service Disconnected")
            }
        })
    }

    private fun queryProducts() {
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(
                productIds.map {
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(it)
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()
                }
            )
            .build()

        billingClient.queryProductDetailsAsync(params) { result: BillingResult, queryResult: QueryProductDetailsResult ->
            if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                productDetailsCache = queryResult.productDetailsList
                Timber.d("Products fetched: ${queryResult.productDetailsList.size}")
            } else {
                Timber.e("Failed to query products: ${result.debugMessage}")
            }
        }
    }

    fun launchPurchaseFlow(activity: Activity, productId: String) {
        val productDetails = productDetailsCache.find { it.productId == productId }

        if (productDetails != null) {
            val productDetailsParamsList = listOf(
                BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(productDetails)
                    .build()
            )

            val billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build()

            billingClient.launchBillingFlow(activity, billingFlowParams)
        } else {
            CoroutineScope(Dispatchers.Main).launch {
                _purchaseEvent.emit("Product details not loaded. Please check internet.")
            }
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            val consumeParams = ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()

            billingClient.consumeAsync(consumeParams) { result, _ ->
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    Timber.d("Donation Consumed")
                    CoroutineScope(Dispatchers.Main).launch {
                        _purchaseEvent.emit("Thank you for your support!")
                    }
                }
            }
        }
    }
}