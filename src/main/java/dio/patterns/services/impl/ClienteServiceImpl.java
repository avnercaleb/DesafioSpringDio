package dio.patterns.services.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dio.patterns.model.Cliente;
import dio.patterns.model.ClienteRepository;
import dio.patterns.model.Endereco;
import dio.patterns.model.EnderecoRepository;
import dio.patterns.services.ClienteService;
import dio.patterns.services.ViaCepService;

@Service
public class ClienteServiceImpl implements ClienteService{
	
	@Autowired
	private ClienteRepository clienteRepository;
	@Autowired
	private EnderecoRepository enderecoRepository;
	@Autowired
	private ViaCepService viaCepService;
	
	@Override
	public Iterable<Cliente> buscarTodos() {
		
		return clienteRepository.findAll();
	}

	@Override
	public Cliente buscarPorId(Long id) {
		
		Optional<Cliente> cliente = clienteRepository.findById(id);
		return cliente.get();
	}

	@Override
	public void inserir(Cliente cliente) {
		
		salvarClienteComCep(cliente);
	}	

	@Override
	public void atualizar(Long id, Cliente cliente) {
		
		// Buscar cliente pelo ID, e verificar se exite
		Optional<Cliente> clienteBd = clienteRepository.findById(id);
		if(clienteBd.isPresent()) {
			
			salvarClienteComCep(cliente); 			
		}
		
	}

	@Override
	public void deletar(Long id) {
		
		Optional<Cliente> cliente = clienteRepository.findById(id);
		if(cliente.isPresent()) {
			clienteRepository.deleteById(id);
		}
	}
	
	private void salvarClienteComCep(Cliente cliente) {
		//Verifica se o endereço ja existe na base (pelo CEP)
		String cep = cliente.getEndereco().getCep();
		
		// Se existir, retorna na variavel endereco
		Endereco endereco = enderecoRepository.findById(cep).orElseGet(() -> {
			
		// Caso não exista, integrar o viaCep e persistir o retorno
			Endereco novoEndereco = viaCepService.consultarCep(cep);
			enderecoRepository.save(novoEndereco);
			return novoEndereco;		
		});
		
		// Salva cliente, vinculado o endereço
		cliente.setEndereco(endereco);
		clienteRepository.save(cliente);
	}

}
