package br.com.techmarket_product_service.repository;

import br.com.techmarket_product_service.model.Produto;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProdutoRepository extends MongoRepository<Produto, String> {
}
