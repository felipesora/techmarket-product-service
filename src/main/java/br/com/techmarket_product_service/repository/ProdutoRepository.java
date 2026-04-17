package br.com.techmarket_product_service.repository;

import br.com.techmarket_product_service.model.Produto;
import br.com.techmarket_product_service.model.enums.CategoriaProduto;
import br.com.techmarket_product_service.model.enums.StatusProduto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProdutoRepository extends MongoRepository<Produto, String> {
    List<Produto> findByCategoria(CategoriaProduto categoria, Sort sort);

    List<Produto> findByNomeContainingIgnoreCase(String nome, Sort sort);

    Page<Produto> findByStatusOrderByQuantidadeVendidaDesc(StatusProduto status, Pageable pageable);

    Page<Produto> findByPrecoPromocionalNotNullOrderByQuantidadeVendidaDesc(StatusProduto status, Pageable pageable);

    long countByStatus(StatusProduto status);

    boolean existsByCodigo(String codigo);
}
