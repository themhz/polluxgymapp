package com.example.personalgymapp.repository

import com.example.personalgymapp.database.dao.ClientDao
import com.example.personalgymapp.database.dao.SubscriptionDao
import com.example.personalgymapp.database.entity.ClientEntity
import com.example.personalgymapp.database.entity.SubscriptionEntity
import kotlinx.coroutines.flow.Flow

class ClientRepository(
    private val clientDao: ClientDao,
    private val subscriptionDao: SubscriptionDao
) {
    val allClients: Flow<List<ClientEntity>> = clientDao.getAllClients()
    val allSubscriptions: Flow<List<SubscriptionEntity>> = subscriptionDao.getAllSubscriptions()

    fun getClientById(id: Int): Flow<ClientEntity?> = clientDao.getClientById(id)
    
    fun getSubscriptionsForClient(clientId: Int): Flow<List<SubscriptionEntity>> = 
        subscriptionDao.getSubscriptionsForClient(clientId)

    suspend fun insertClient(client: ClientEntity) {
        clientDao.insertClient(client)
    }

    suspend fun updateClient(client: ClientEntity) {
        clientDao.updateClient(client)
    }

    suspend fun deleteClient(client: ClientEntity) {
        clientDao.deleteClient(client)
    }

    suspend fun insertSubscription(subscription: SubscriptionEntity) {
        subscriptionDao.insertSubscription(subscription)
    }

    suspend fun updateSubscription(subscription: SubscriptionEntity) {
        subscriptionDao.updateSubscription(subscription)
    }

    suspend fun deleteSubscription(subscription: SubscriptionEntity) {
        subscriptionDao.deleteSubscription(subscription)
    }
}
