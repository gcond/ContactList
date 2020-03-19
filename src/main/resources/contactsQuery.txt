SELECT c.contact_id,
c.first_name,
c.last_name,
pnm.phone_type,pnm.country_code,pnm.phone_number,
pnw.phone_type,pnw.country_code,pnw.phone_number,
pnh.phone_type,pnh.country_code,pnh.phone_number,
c.email,
c.is_favorite
FROM contacts c
LEFT JOIN phonenumbers pnm
ON c.contact_id = pnm.contact_id AND pnm.phone_type = 'Mobile'
LEFT JOIN phonenumbers pnw
ON c.contact_id = pnw.contact_id AND pnw.phone_type = 'Work'
LEFT JOIN phonenumbers pnh
ON c.contact_id = pnh.contact_id AND pnh.phone_type = 'Home'