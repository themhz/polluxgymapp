package com.example.personalgymapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.personalgymapp.data.mockClients
import com.example.personalgymapp.data.mockSubscriptions
import com.example.personalgymapp.database.entity.ClientEntity
import com.example.personalgymapp.database.entity.PaymentEntity
import com.example.personalgymapp.database.entity.SubscriptionEntity
import com.example.personalgymapp.model.Payment
import com.example.personalgymapp.model.Subscription
import com.example.personalgymapp.repository.ClientRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ClientViewModel(private val repository: ClientRepository) : ViewModel() {

    val clients: StateFlow<List<ClientEntity>> = repository.allClients
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val subscriptions: StateFlow<List<Subscription>> = repository.allSubscriptions
        .map { entities -> entities.map { it.toDomainModel() } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun getPaymentsForClient(clientId: Int): Flow<List<Payment>> =
        repository.getPaymentsForClient(clientId).map { entities ->
            entities.map { it.toDomainModel() }
        }

    fun addPayment(payment: Payment) {
        viewModelScope.launch {
            repository.insertPayment(PaymentEntity.fromDomainModel(payment))
            
            // Optionally update totalPaid in subscription
            val clientSubs = repository.getSubscriptionsForClient(payment.clientId).first()
            val activeSub = clientSubs.find { it.status != "Paid" } ?: clientSubs.lastOrNull()
            
            if (activeSub != null) {
                val updatedSub = activeSub.copy(
                    totalPaid = activeSub.totalPaid + payment.amount,
                    status = if (activeSub.totalPaid + payment.amount >= activeSub.price) "Paid" else activeSub.status
                )
                repository.updateSubscription(updatedSub)
            }
        }
    }

    fun seedDatabaseIfEmpty() {
        viewModelScope.launch {
            val currentClients = repository.allClients.first()
            if (currentClients.isEmpty()) {
                mockClients.forEach {
                    repository.insertClient(it)
                }
            }
            
            val currentSubs = repository.allSubscriptions.first()
            if (currentSubs.isEmpty()) {
                mockSubscriptions.forEach {
                    repository.insertSubscription(SubscriptionEntity.fromDomainModel(it))
                }
            }
        }
    }

    fun addSubscription(subscription: Subscription) {
        viewModelScope.launch {
            repository.insertSubscription(SubscriptionEntity.fromDomainModel(subscription))
        }
    }

    fun updateSubscription(subscription: Subscription) {
        viewModelScope.launch {
            repository.updateSubscription(SubscriptionEntity.fromDomainModel(subscription))
        }
    }

    fun deleteSubscription(subscription: Subscription) {
        viewModelScope.launch {
            repository.deleteSubscription(SubscriptionEntity.fromDomainModel(subscription))
        }
    }

    fun addClient(client: ClientEntity) {
        viewModelScope.launch {
            repository.insertClient(client)
        }
    }

    fun updateClient(client: ClientEntity) {
        viewModelScope.launch {
            repository.updateClient(client)
        }
    }

    fun deleteClient(client: ClientEntity) {
        viewModelScope.launch {
            repository.deleteClient(client)
        }
    }
}
