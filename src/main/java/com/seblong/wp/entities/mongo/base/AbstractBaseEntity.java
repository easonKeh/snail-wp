package com.seblong.wp.entities.mongo.base;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import com.seblong.wp.enums.EntityStatus;


public class AbstractBaseEntity {

	@Id
	protected ObjectId id;

	@Indexed
	protected long created;

	@Indexed
	protected EntityStatus status;

	@Indexed
	protected long updated;

	public AbstractBaseEntity() {
		this.status = EntityStatus.ACTIVED;
		this.created = System.currentTimeMillis();
		this.updated = System.currentTimeMillis();
	}

	public AbstractBaseEntity(EntityStatus status, Long created, Long updated) {
		if (status != null) {
			this.status = status;
		} else {
			this.status = EntityStatus.ACTIVED;
		}
		if (created != null)
			this.created = created;
		if (updated != null)
			this.updated = updated;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public long getCreated() {
		return created;
	}

	public void setCreated(long created) {
		this.created = created;
	}

	public EntityStatus getStatus() {
		return status;
	}

	public void setStatus(EntityStatus status) {
		this.status = status;
	}

	public long getUpdated() {
		return updated;
	}

	public void setUpdated(long updated) {
		this.updated = updated;
	}

}
