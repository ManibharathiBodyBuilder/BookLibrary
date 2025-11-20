package com.booklibrary.specificationbook;



import org.springframework.data.jpa.domain.Specification;
import com.booklibrary.entity.BookEntity;
import com.booklibrary.entity.MyBookEntity;

public class BookSpecification {

    public static Specification<BookEntity> hasBookName(String bookName) {
        return (root, query, cb) ->
                (bookName == null || bookName.isEmpty()) ? null :
                        cb.like(cb.lower(root.get("bookName")), "%" + bookName.toLowerCase() + "%");
    }
    
    public static Specification<MyBookEntity> hasMyBookNames(String name) {
        return (root, query, cb) -> {
            if (name == null || name.trim().isEmpty()) {
                return cb.conjunction();  // return all
            }
            return cb.like(
                cb.lower(root.get("bookName")), 
                "%" + name.toLowerCase() + "%"
            );
        };
    }

/*    public static Specification<BookEntity> hasBookAuthor(String bookAuthor) {
        return (root, query, cb) ->
                (bookAuthor == null || bookAuthor.isEmpty()) ? null :
                        cb.like(cb.lower(root.get("bookAuthor")), "%" + bookAuthor.toLowerCase() + "%");
    }*/

/*    public static Specification<BookEntity> hasBookLanch(String bookLanch) {
        return (root, query, cb) ->
                (bookLanch == null || bookLanch.isEmpty()) ? null :
                        cb.like(cb.lower(root.get("BookLanch")), "%" + bookLanch.toLowerCase() + "%");
    }*/
}

