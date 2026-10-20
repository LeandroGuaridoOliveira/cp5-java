package br.com.fiap.mercadomvc.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "TDS_Sec_MVC_TB_Mercado")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @NotBlank(message = "O nome do produto é obrigatório.")
    @Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres.")
    @Column(name = "NOME", nullable = false, length = 100)
    private String nome;

    @NotBlank(message = "O tipo é obrigatório.")
    @Size(max = 50, message = "O tipo deve ter até 50 caracteres.")
    @Column(name = "TIPO", nullable = false, length = 50)
    private String tipo;

    @NotBlank(message = "O setor é obrigatório.")
    @Size(max = 50, message = "O setor deve ter até 50 caracteres.")
    @Column(name = "SETOR", nullable = false, length = 50)
    private String setor;

    @Size(max = 30, message = "O tamanho deve ter até 30 caracteres.")
    @Column(name = "TAMANHO", length = 30)
    private String tamanho;

    @NotNull(message = "O preço é obrigatório.")
    @Positive(message = "O preço deve ser maior que zero.")
    @Column(name = "PRECO", nullable = false)
    private Double preco;

    @Column(name = "ESTOQUE")
    private Integer estoque = 50;

    public Produto() {
        this.estoque = 50;
    }

    public Produto(Long id, String nome, String tipo, String setor, String tamanho, Double preco) {
        this(id, nome, tipo, setor, tamanho, preco, 50);
    }

    public Produto(Long id, String nome, String tipo, String setor, String tamanho, Double preco, Integer estoque) {
        this.id = id;
        this.nome = nome;
        this.tipo = tipo;
        this.setor = setor;
        this.tamanho = tamanho;
        this.preco = preco;
        this.estoque = estoque != null ? estoque : 50;
    }

    public Produto(String nome, String tipo, String setor, String tamanho, Double preco) {
        this(null, nome, tipo, setor, tamanho, preco, 50);
    }

    public Produto(String nome, String tipo, String setor, String tamanho, Double preco, Integer estoque) {
        this(null, nome, tipo, setor, tamanho, preco, estoque);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getSetor() {
        return setor;
    }

    public void setSetor(String setor) {
        this.setor = setor;
    }

    public String getTamanho() {
        return tamanho;
    }

    public void setTamanho(String tamanho) {
        this.tamanho = tamanho;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    public Integer getEstoque() {
        return estoque != null ? estoque : 50;
    }

    public void setEstoque(Integer estoque) {
        this.estoque = estoque != null ? estoque : 0;
    }
}
