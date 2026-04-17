package br.com.techmarket_product_service.dto.produto;

import br.com.techmarket_product_service.model.enums.CategoriaProduto;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProdutoCreateDTO(
        @NotBlank(message = "O código do produto é obrigatório")
        @Size(min = 3, max = 50, message = "O código deve ter entre 3 e 50 caracteres")
        String codigo,

        @NotBlank(message = "O nome do produto é obrigatório")
        @Size(min = 3, max = 150, message = "O nome deve ter entre 3 e 150 caracteres")
        String nome,

        @Size(max = 500, message = "A descrição pode ter no máximo 500 caracteres")
        String descricao,

        @NotNull(message = "A categoria é obrigatória")
        CategoriaProduto categoria,

        @Size(max = 100, message = "A marca pode ter no máximo 100 caracteres")
        String marca,

        @NotNull(message = "O preço unitário é obrigatório")
        @Positive(message = "O preço unitário deve ser maior que zero")
        BigDecimal precoUnitario,

        @Positive(message = "O preço promocional deve ser maior que zero")
        BigDecimal precoPromocional,

        @NotNull(message = "O estoque é obrigatório")
        @PositiveOrZero(message = "O estoque não pode ser negativo")
        Integer estoque
) {}
