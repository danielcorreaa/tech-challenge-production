package com.techchallenge.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "product")
public class ProductEntity {

	@Id
	@Column(name = "id", nullable = false)
	private String id;
	private String sku;
	private String title;
	private String category;
	private String description;
	private String image;
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "production_orderId")
	private ProductionEntity production;



}
