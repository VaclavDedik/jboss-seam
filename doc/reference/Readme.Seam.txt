===================
INDEX
===================

Please add index references while typing documentation:

To do si include the indexterm tag, it can be added anywhere in the docbook:
<indexterm>
   <primary>@Entity</primary>
</indexterm>

You can have categories:
<indexterm>
   <primary>Annotation</primary>
   <secondary>entity</secondary>
</indexterm>

For Annotations, please use only the following and index everywhere you explain something about it: 
<indexterm>
   <primary>@Entity</primary>
</indexterm>

annotation.xml defines other index entries forwarding to @xxxxx

===================
SECTION
===================
Please use <section> instead of <sect1> <sect2> ... so they can be more easily moved from one level to the other.
Respect indentation for a better readability



Thomas.