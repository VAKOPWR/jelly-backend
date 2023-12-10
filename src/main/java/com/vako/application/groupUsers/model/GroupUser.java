package com.vako.application.groupUsers.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.google.api.client.util.DateTime;
import com.vako.application.group.model.Group;
import com.vako.application.user.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Table(name = "Group_User")
public class GroupUser {

    public GroupUser(final User user,
                     final Group group) {
        this.user = user;
        this.group = group;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "group_id", referencedColumnName = "id", nullable = false)
    private Group group;

    @Column(name = "is_muted", nullable = false)
    @ColumnDefault(value = "false")
    private boolean isMuted = false;

    @Column(name = "is_pinned", nullable = false)
    @ColumnDefault(value = "false")
    private boolean isPinned = false;

    @Column(name = "last_checked")
    private LocalDateTime lastChecked;
}

