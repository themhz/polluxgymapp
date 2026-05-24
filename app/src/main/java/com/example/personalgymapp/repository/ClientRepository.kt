package com.example.personalgymapp.repository

import com.example.personalgymapp.database.dao.ClientDao
import com.example.personalgymapp.database.entity.ClientEntity
import kotlinx.coroutines.flow.Flow

class ClientRepository(private val clientDao: ClientDao) {
    val allClients: Flow<List<ClientEntity>> = clientDao.getAllClients()

    fun getClientById(id: Int): Flow<ClientEntity?> = clientDao.getClientById(id)

    suspend fun insertClient(client: ClientEntity) {
        clientDao.insertClient(client)
    }

    suspend fun updateClient(client: ClientEntity) {
        clientDao.updateClient(client)
    }

    suspend fun deleteClient(client: ClientEntity) {
        clientDao.deleteClient(client)
    }
}
