package com.example.dweb_App.web.controllers;

import com.example.dweb_App.data.entities.Client;
import com.example.dweb_App.data.service.ClientService;
import com.example.dweb_App.dto.request.ClientUpdateDto;
import com.example.dweb_App.exception.EntityNotFoundException;
import com.example.dweb_App.exception.UpdateFailedException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/clientInfos")

public class ClientController {

    private ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<?> saveClientInfos(@RequestBody @Valid ClientUpdateDto client) {

        if (clientService.loadClient(client.getFullName()).isEmpty()) {

            Client newClient= Client.builder()
                    .cin(client.getCin())
                    .ville(client.getVille())
                    .adresse(client.getAdresse())
                    .email(client.getEmail())
                    .contrat(client.getContrat())
                    .fullName(client.getFullName())
                    .phoneNumber(client.getPhoneNumber())
                    .reseauSocial(client.getReseauSocial())
                    .build();

            Client savedClient=clientService.saveClient(newClient);
            return ResponseEntity.ok("Client saved successfully");
        } else {
            return ResponseEntity.ok("This client already exists");
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public ResponseEntity<List<Client>> getClients() {
        List<Client> clients= clientService.allClients();
        if(!clients.isEmpty()) {
            return ResponseEntity.ok(clients);
        }
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping(path="/{clientCin}")
    public ResponseEntity<?> deleteClient(@PathVariable String clientCin) {
        Client client = clientService.loadClientByCin(clientCin)
                .orElseThrow(()->new EntityNotFoundException("Client not Found "+clientCin));


        clientService.deleteClientByCin(clientCin);

        return ResponseEntity.ok(Map.of(
                "message", "Client deleted successfully",
                "clientName", client.getFullName()
        ));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping(path="/{clientCin}")
    public ResponseEntity<?> editClientInfos(@PathVariable String clientCin,@RequestBody @Valid ClientUpdateDto client){

        try {
            Client existingClient = clientService.loadClientByCin(clientCin)
                    .orElseThrow(() -> new EntityNotFoundException("Client not Found " + clientCin));
            existingClient.setEmail(client.getEmail());
            existingClient.setPhoneNumber(client.getPhoneNumber());
            existingClient.setAdresse(client.getAdresse());
            existingClient.setVille(client.getVille());
            existingClient.setFullName(client.getFullName());
            existingClient.setContrat(client.getContrat());
            existingClient.setReseauSocial(client.getReseauSocial());

            log.info("Updating client {} (CIN: {})", existingClient.getFullName(), clientCin);

            Client updatedClient = clientService.saveClient(existingClient);

            return ResponseEntity.ok(updatedClient);

        }catch (DataIntegrityViolationException e){
            throw new UpdateFailedException("Update would violate database constraints: "+e.getMessage(),e);
        }catch (Exception e){
            throw new UpdateFailedException("Failed to update client: "+e.getMessage(),e);
        }
    }
}
