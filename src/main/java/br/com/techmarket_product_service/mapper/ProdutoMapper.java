package br.com.techmarket_product_service.mapper;

import br.com.techmarket_product_service.dto.produto.ProdutoCreateDTO;
import br.com.techmarket_product_service.dto.produto.ProdutoResponseDTO;
import br.com.techmarket_product_service.dto.produto.ProdutoUpdateDTO;
import br.com.techmarket_product_service.dto.produtoSnapshot.ProdutoSnapshotDTO;
import br.com.techmarket_product_service.model.Produto;
import br.com.techmarket_product_service.model.enums.StatusProduto;

import java.time.LocalDateTime;

public final class ProdutoMapper {

    private ProdutoMapper () {}

    public static Produto converterCreateDTOParaEntity(ProdutoCreateDTO dto) {

        Produto produto = new Produto();
        produto.setCodigo(dto.codigo());
        produto.setNome(dto.nome());
        produto.setDescricao(dto.descricao());
        produto.setCategoria(dto.categoria());
        produto.setMarca(dto.marca());
        produto.setPreco(dto.preco());
        produto.setEstoque(dto.estoque());
        produto.setStatus(StatusProduto.ATIVO);
        produto.setDataCriacao(LocalDateTime.now());
        produto.setQuantidadeVendida(0);

        return produto;
    }

    public static Produto converterUpdateDTOParaEntity(ProdutoUpdateDTO dto, Produto produto) {

        produto.setCodigo(dto.codigo());
        produto.setNome(dto.nome());
        produto.setDescricao(dto.descricao());
        produto.setCategoria(dto.categoria());
        produto.setMarca(dto.marca());
        produto.setPreco(dto.preco());
        produto.setEstoque(dto.estoque());
        produto.setStatus(dto.status());
        produto.setDataCriacao(produto.getDataCriacao());

        return produto;
    }

    public static ProdutoResponseDTO converterParaResponseDTO(Produto produto) {
        return new ProdutoResponseDTO(
                produto.getId(),
                produto.getCodigo(),
                produto.getNome(),
                produto.getDescricao(),
                produto.getCategoria(),
                produto.getMarca(),
                produto.getPreco(),
                produto.getEstoque(),
                produto.getStatus(),
                produto.getDataCriacao(),
                produto.getImagemId(),
                produto.getQuantidadeVendida()
        );
    }

    public static ProdutoSnapshotDTO converterParaProdutoSnapshot(Produto produto) {
        return new ProdutoSnapshotDTO(
                produto.getId(),
                produto.getCodigo(),
                produto.getNome(),
                produto.getPreco(),
                produto.getEstoque(),
                produto.getStatus()
        );
    }
}
