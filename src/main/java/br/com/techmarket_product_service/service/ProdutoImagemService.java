package br.com.techmarket_product_service.service;

import br.com.techmarket_product_service.dto.imagem.ImagemResponseDTO;
import br.com.techmarket_product_service.exception.EntityNotFoundException;
import br.com.techmarket_product_service.model.Produto;
import br.com.techmarket_product_service.repository.ProdutoRepository;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
public class ProdutoImagemService {

    private final GridFsTemplate gridFsTemplate;
    private final ProdutoRepository produtoRepository;

    public ProdutoImagemService(GridFsTemplate gridFsTemplate, ProdutoRepository produtoRepository) {
        this.gridFsTemplate = gridFsTemplate;
        this.produtoRepository = produtoRepository;
    }

    public String salvarImagem(String produtoId, InputStream inputStream, String nomeArquivo, String contentType) {
        try {
            Produto produto = produtoRepository.findById(produtoId)
                    .orElseThrow(() -> new EntityNotFoundException("Produto com id: " + produtoId + " não encontrado"));

            // se já tem imagem, remove do GridFS
            if (produto.getImagemId() != null) {
                gridFsTemplate.delete(Query.query(Criteria.where("_id").is(new ObjectId(produto.getImagemId()))));
            }

            // salva no GridFS
            ObjectId fileId = gridFsTemplate.store(
              inputStream,
              nomeArquivo,
              contentType
            );

            // salva referencia no produto
            produto.setImagemId(fileId.toString());
            produtoRepository.save(produto);

            return fileId.toString();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar imagem", e);
        }
    }

    public String salvarImagemMultipart(String produtoId, MultipartFile file) {
        try {
            return salvarImagem(produtoId, file.getInputStream(), file.getOriginalFilename(), file.getContentType());
        } catch (IOException e) {
            throw new RuntimeException("Erro ao processar arquivo", e);
        }
    }

    public ImagemResponseDTO buscarImagem(String imagemId) {
        try {
            GridFSFile file = gridFsTemplate.findOne(
                    Query.query(Criteria.where("_id").is(new ObjectId(imagemId)))
            );

            if (file == null) {
                throw new EntityNotFoundException("Imagem não encontrada");
            }

            GridFsResource resource = gridFsTemplate.getResource(file);

            return new ImagemResponseDTO(
                    resource.getContentAsByteArray(),
                    resource.getContentType()
            );

        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler imagem", e);
        }
    }
}
