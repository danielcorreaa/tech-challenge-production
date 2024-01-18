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
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String sku;
	private String title;
	private String category;
	private String description;
	private String image;
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "production_orderId")
	private ProductionEntity production;


}
