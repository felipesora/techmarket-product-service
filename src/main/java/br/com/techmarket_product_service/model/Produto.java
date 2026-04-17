package br.com.techmarket_product_service.model;

import br.com.techmarket_product_service.model.enums.CategoriaProduto;
import br.com.techmarket_product_service.model.enums.StatusProduto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document(collection = "produtos")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Produto {

    @Id
    private String id;

    @Indexed(unique = true)
    private String codigo;

    private String nome;

    private String descricao;

    private CategoriaProduto categoria;

    private String marca;

    private BigDecimal precoUnitario;

    private BigDecimal precoPromocional;

    private Integer estoque;

    private StatusProduto status;

    private LocalDateTime dataCriacao;

    private String imagemId;

    private Integer quantidadeVendida;
}
