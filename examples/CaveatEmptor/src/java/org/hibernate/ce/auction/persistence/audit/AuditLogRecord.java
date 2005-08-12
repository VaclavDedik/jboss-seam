package org.hibernate.ce.auction.persistence.audit;

import javax.persistence.*;
import java.util.Date;

/**
 * A trivial audit log record.
 * <p>
 * This simple value class represents a single audit event.
 *
 * @author Christian Bauer <christian@hibernate.org>
 */
@Entity(access = AccessType.FIELD)
@Table(name = "AUDIT_LOG")
public class AuditLogRecord implements java.io.Serializable {

    @Id(generate = GeneratorType.AUTO)
	@Column(name = "AUDIT_LOG_ID")
    private Long id = null;

	@Column(length = 255, nullable = false)
    public String message;

    @Column(name = "ENTITY_ID", nullable = false)
	public Long entityId;

    @Column(name = "ENTITY_CLASS", nullable = false)
	public Class entityClass;

    @Column(name = "USER_ID", nullable = false)
	public Long userId;

	@Column(nullable = false)
    public Date created;

	AuditLogRecord() {}

	public AuditLogRecord(String message,
						  Long entityId,
						  Class entityClass,
						  Long userId) {
		this.message = message;
		this.entityId = entityId;
		this.entityClass = entityClass;
		this.userId = userId;
		this.created = new Date();
	}

    public Long getId() { return id; }

}
