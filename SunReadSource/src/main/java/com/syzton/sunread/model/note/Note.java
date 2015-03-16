package com.syzton.sunread.model.note;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import com.syzton.sunread.dto.note.NoteDTO;
import com.syzton.sunread.model.book.Book;

import javax.persistence.*;

import java.util.Collection;
import java.util.Set;


/**
 * @author chenty
 *
 */
@Entity
@Table(name="note")
public class Note {

    public static final int MAX_LENGTH_TITLE = 200;
    public static final int MAX_LENGTH_CONTENT = 200000;
    public static final int MAX_LENGTH_IMAGE = 10485760; // 10MiB
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

	@Column(name="title",length = MAX_LENGTH_TITLE)
    private String title;
    
    @Column(name = "create_time", nullable = false)
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime createTime;

    @Column(name="content",length = MAX_LENGTH_CONTENT)
    private String content;
    
    @OneToMany(cascade = CascadeType.ALL,mappedBy = "note")
    @Basic(fetch = FetchType.LAZY)
    private Set<Comment> comments;
    
	@ManyToOne(cascade={CascadeType.MERGE,CascadeType.REFRESH }, optional = false)
    @Basic(fetch = FetchType.LAZY)
    @JoinColumn(name="book_id")
	private Book book;

    @Column(name="image",length = MAX_LENGTH_IMAGE)
    private String image;
    
    public Note() {

    }

    public static Builder getBuilder(String title, String content, Book book) {
        return new Builder(title, content, book);
    }

    public Long getId() {
        return id;   
    }

    public String getTitle() {
		return title;
	}

	public DateTime getCreateTime() {
		return createTime;
	}

	public String getContent() {
		return content;
	}

	public String getImage() {
		return image;
	}
    
    public void update(String title, String content, String image) {
        this.title = title;
        this.content = content;
        this.image = image;
    }

    @PrePersist
    public void prePersist() {
        DateTime now = DateTime.now();
        createTime = now;
    }

    
    public static class Builder {

        private Note built;

        public Builder(String title, String content, Book book) {
            built = new Note();
            built.title = title;
            built.content = content;
            built.book = book;
        }

        public Note build() {
            return built;
        }

        public Builder image(String image){
        	built.image = image;
        	return this;
        }
    }

    public NoteDTO createDTO(Note model) {
        NoteDTO dto = new NoteDTO();

        dto.setId(model.getId());
        dto.setTitle(model.getTitle());
        dto.setContent(model.getContent());
        dto.setImage(model.getImage());

        return dto;
    }

    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}