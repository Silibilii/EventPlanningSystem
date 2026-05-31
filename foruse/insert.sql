
INSERT INTO eventschema.client (client_name, contact_person, phone, email, is_legal, organization_name, notes) VALUES
    ('Семья Петровых', 'Иван Петров', '+7(910)123-45-67', 'petrov.family@mail.ru', FALSE, NULL, 'Частное лицо, свадьба дочери'),
    ('ООО "Муромский завод"', 'Мария Кузнецова', '+7(910)555-66-77', 'muromzavod@mail.ru', TRUE, 'ООО "Муромский завод"', 'Юбилей директора'),
    ('ИП Смирнов Алексей', 'Алексей Смирнов', '+7(910)777-88-99', 'asmirnov@it.ru', FALSE, NULL, 'IT-конференция')
ON CONFLICT DO NOTHING;

INSERT INTO eventschema.venue (venue_name, address, capacity, rental_cost, contact_phone) VALUES
    ('Банкетный зал "Уют"', 'г. Муром, ул. Ленина, д. 10', 50, 15000.00, '+7(4922)55-66-77'),
    ('Конференц-холл "Премьер"', 'г. Владимир, пр-т Ленина, д. 25', 200, 50000.00, '+7(4922)22-33-44'),
    ('Ресторан "Старый город"', 'г. Муром, ул. Московская, д. 88', 80, 30000.00, '+7(4922)44-55-66'),
    ('Лофт-пространство "Свобода"', 'г. Муром, ул. Советская, д. 5', 120, 40000.00, '+7(4922)99-88-77'),
    ('Загородный клуб "Берёзка"', 'г. Муром, Сосновый пер., 7', 150, 60000.00, '+7(4922)33-22-11')
ON CONFLICT DO NOTHING;

INSERT INTO eventschema.contractor (contractor_name, contact_person, phone, email, service_type, price_list, notes) VALUES

    ('Антон Сидоров (ведущий)', 'Антон Сидоров', '+7(910)444-55-66', 'anton@show.ru', 'ведущий', 'Вечер до 5 часов – 15000 руб.', 'Работает с аудиторией'),
    ('Студия праздника "Виват"', 'Ольга Виноградова', '+7(910)123-12-12', 'vivat@holiday.ru', 'ведущий', 'Программа под ключ – от 25000 руб.', 'Команда профессиональных ведущих'),

    ('Елена Васильева (декор)', 'Елена Васильева', '+7(910)777-88-99', 'elena@decor.ru', 'декоратор', 'Оформление зала – от 10000 руб.', 'Шары, цветы, ткани'),
    ('Студия декора "Арт-Стиль"', 'Мария Соколова', '+7(910)222-33-44', 'artstyle@decor.ru', 'декоратор', 'Флористика, арки – от 20000 руб.', 'Индивидуальный дизайн'),

    ('Кафе "Шеф-повар"', 'Игорь Мясоедов', '+7(910)666-77-88', 'chef@cafe.ru', 'кейтеринг', 'Банкет от 2000 руб./чел.', 'Выездное обслуживание'),
    ('Ресторан "Вкусно и точка"', 'Елена Менеджер', '+7(910)222-33-44', 'catering@vkusno.ru', 'кейтеринг', 'Фуршет от 1500 руб./гость', 'Скидка 10% при заказе от 50 человек'),
    ('Агентство праздников "Фуршет"', 'Сергей Петров', '+7(910)555-12-34', 'furshet@party.ru', 'кейтеринг', 'Горячие блюда, закуски – от 1200 руб./чел.', 'Доставка и обслуживание'),

    ('Мария Фото', 'Мария Фото', '+7(910)999-00-11', 'maria@photo.ru', 'фотограф', '3 часа съёмки – 7000 руб.', 'Репортажная съёмка'),
    ('Александр Петров (фото)', 'Александр Петров', '+7(910)111-22-33', 'photo@example.com', 'фотограф', '2 часа – 5000 руб.', 'Выездная съёмка'),
    ('Фотостудия "Момент"', 'Дмитрий Козлов', '+7(910)333-44-55', 'moment@photo.ru', 'фотограф', 'Свадебный пакет – 15000 руб.', 'Love story и репортаж'),

    ('Дмитрий Звук', 'Дмитрий Звук', '+7(910)123-45-67', 'sound@audio.ru', 'звукорежиссёр', 'Оборудование + работа – 12000 руб.', 'Колонки, микрофоны'),
    ('Студия звука "Акустика"', 'Андрей Громов', '+7(910)444-55-66', 'akustika@sound.ru', 'звукорежиссёр', 'Полный комплект – 20000 руб.', 'Профессиональное оборудование'),

    ('Веб-студия "Айтишник"', 'Алексей Код', '+7(910)123-45-67', 'info@itishnik.ru', 'прочее', 'Разработка сайта под ключ – от 50000 руб.', 'Создание сайтов, интернет-магазинов'),

    ('Транспортная компания "Поехали"', 'Иван Водитель', '+7(910)777-11-22', 'poehali@taxi.ru', 'прочее', 'Микроавтобус для гостей – 5000 руб./рейс', 'Свадебный кортеж')
ON CONFLICT DO NOTHING;

INSERT INTO eventschema.event (event_name, event_date, budget, status, feedback, client_id, venue_id)
SELECT 
    'Юбилей директора ООО "Муромский завод"',
    '2025-12-20',
    500000.00,
    'завершено',
    'Спасибо большое за отличную организацию! Всё прошло на высшем уровне. Гости в восторге.',
    (SELECT client_id FROM eventschema.client WHERE client_name = 'ООО "Муромский завод"'),
    (SELECT venue_id FROM eventschema.venue WHERE venue_name = 'Ресторан "Старый город"')
WHERE NOT EXISTS (SELECT 1 FROM eventschema.event WHERE event_name = 'Юбилей директора ООО "Муромский завод"');

INSERT INTO eventschema.task (description, deadline, status, event_id, responsible_contractor_id) VALUES
    ('Согласование даты и места проведения', '2025-10-01', 'выполнено', (SELECT event_id FROM eventschema.event WHERE event_name = 'Юбилей директора ООО "Муромский завод"'), NULL),
    ('Выбор кейтеринговой компании', '2025-10-15', 'выполнено', (SELECT event_id FROM eventschema.event WHERE event_name = 'Юбилей директора ООО "Муромский завод"'), NULL),
    ('Составление меню', '2025-11-01', 'выполнено', (SELECT event_id FROM eventschema.event WHERE event_name = 'Юбилей директора ООО "Муромский завод"'), (SELECT contractor_id FROM eventschema.contractor WHERE contractor_name = 'Кафе "Шеф-повар"')),
    ('Оформление зала', '2025-12-10', 'выполнено', (SELECT event_id FROM eventschema.event WHERE event_name = 'Юбилей директора ООО "Муромский завод"'), (SELECT contractor_id FROM eventschema.contractor WHERE contractor_name = 'Елена Васильева (декор)')),
    ('Подбор ведущего', '2025-11-10', 'выполнено', (SELECT event_id FROM eventschema.event WHERE event_name = 'Юбилей директора ООО "Муромский завод"'), NULL),
    ('Заказ фотографа', '2025-11-20', 'выполнено', (SELECT event_id FROM eventschema.event WHERE event_name = 'Юбилей директора ООО "Муромский завод"'), NULL),
    ('Рассылка приглашений гостям', '2025-11-25', 'выполнено', (SELECT event_id FROM eventschema.event WHERE event_name = 'Юбилей директора ООО "Муромский завод"'), NULL),
    ('Финальная проверка всех услуг', '2025-12-18', 'выполнено', (SELECT event_id FROM eventschema.event WHERE event_name = 'Юбилей директора ООО "Муромский завод"'), NULL)
ON CONFLICT DO NOTHING;

INSERT INTO eventschema.expense (amount, expense_date, description, event_id, contractor_id, category) VALUES
    (30000.00, '2025-10-05', 'Аренда ресторана (предоплата)', (SELECT event_id FROM eventschema.event WHERE event_name = 'Юбилей директора ООО "Муромский завод"'), NULL, 'аренда'),
    (100000.00, '2025-11-01', 'Кейтеринг (предоплата)', (SELECT event_id FROM eventschema.event WHERE event_name = 'Юбилей директора ООО "Муромский завод"'), (SELECT contractor_id FROM eventschema.contractor WHERE contractor_name = 'Кафе "Шеф-повар"'), 'кейтеринг'),
    (150000.00, '2025-12-10', 'Кейтеринг (основной расчёт)', (SELECT event_id FROM eventschema.event WHERE event_name = 'Юбилей директора ООО "Муромский завод"'), (SELECT contractor_id FROM eventschema.contractor WHERE contractor_name = 'Кафе "Шеф-повар"'), 'кейтеринг'),
    (25000.00, '2025-11-15', 'Услуги декоратора', (SELECT event_id FROM eventschema.event WHERE event_name = 'Юбилей директора ООО "Муромский завод"'), (SELECT contractor_id FROM eventschema.contractor WHERE contractor_name = 'Елена Васильева (декор)'), 'декор'),
    (15000.00, '2025-12-15', 'Оплата ведущего', (SELECT event_id FROM eventschema.event WHERE event_name = 'Юбилей директора ООО "Муромский завод"'), (SELECT contractor_id FROM eventschema.contractor WHERE contractor_name = 'Антон Сидоров (ведущий)'), 'ведущий'),
    (7000.00, '2025-12-18', 'Фотограф', (SELECT event_id FROM eventschema.event WHERE event_name = 'Юбилей директора ООО "Муромский завод"'), (SELECT contractor_id FROM eventschema.contractor WHERE contractor_name = 'Мария Фото'), 'фотограф'),
    (5000.00, '2025-12-19', 'Транспорт', (SELECT event_id FROM eventschema.event WHERE event_name = 'Юбилей директора ООО "Муромский завод"'), (SELECT contractor_id FROM eventschema.contractor WHERE contractor_name = 'Транспортная компания "Поехали"'), 'транспорт'),
    (138000.00, '2025-12-20', 'Кейтеринг (дополнительная оплата)', (SELECT event_id FROM eventschema.event WHERE event_name = 'Юбилей директора ООО "Муромский завод"'), (SELECT contractor_id FROM eventschema.contractor WHERE contractor_name = 'Кафе "Шеф-повар"'), 'кейтеринг')
ON CONFLICT (expense_id) DO NOTHING;

INSERT INTO eventschema.guest (full_name, contact_phone, contact_email, invitation_status, event_id)
SELECT g.full_name, g.phone, g.email, g.status, ev.event_id
FROM (VALUES
    ('Иванов Сергей', '+7(910)111-11-11', 'ivanov@mail.ru', 'подтвердил'),
    ('Петрова Ольга', '+7(910)222-22-22', 'petrova@mail.ru', 'подтвердил'),
    ('Сидоров Алексей', '+7(910)333-33-33', 'sidorov@mail.ru', 'подтвердил'),
    ('Кузнецова Мария', '+7(910)444-44-44', 'kuznecova@mail.ru', 'оплатил'),
    ('Смирнов Дмитрий', '+7(910)555-55-55', 'smirnov@mail.ru', 'оплатил'),
    ('Васильева Елена', '+7(910)666-66-66', 'vasileva@mail.ru', 'оплатил'),
    ('Михайлов Игорь', '+7(910)777-77-77', 'mihailov@mail.ru', 'приглашён'),
    ('Фёдорова Анна', '+7(910)888-88-88', 'fedorova@mail.ru', 'приглашён'),
    ('Николаев Павел', '+7(910)999-99-99', 'nikolaev@mail.ru', 'приглашён'),
    ('Александрова Татьяна', '+7(910)000-00-00', 'aleksandrova@mail.ru', 'приглашён'),
    ('Егоров Владимир', '+7(910)111-11-12', 'egorov@mail.ru', 'отказался'),
    ('Морозова Ирина', '+7(910)222-22-23', 'morozova@mail.ru', 'отказался'),
    ('Соколов Андрей', '+7(910)333-33-34', 'sokolov@mail.ru', 'подтвердил'),
    ('Лебедева Екатерина', '+7(910)444-44-45', 'lebedeva@mail.ru', 'оплатил'),
    ('Новиков Денис', '+7(910)555-55-56', 'novikov@mail.ru', 'приглашён'),
    ('Козлова Ольга', '+7(910)666-66-67', 'kozlova@mail.ru', 'подтвердил'),
    ('Медведев Сергей', '+7(910)777-77-78', 'medvedev@mail.ru', 'приглашён'),
    ('Антонова Наталья', '+7(910)888-88-89', 'antonova@mail.ru', 'оплатил'),
    ('Тарасов Илья', '+7(910)999-99-90', 'tarasov@mail.ru', 'подтвердил'),
    ('Белова Марина', '+7(910)000-00-01', 'belova@mail.ru', 'приглашён'),
    ('Орлов Виктор', '+7(910)111-11-22', 'orlov@mail.ru', 'подтвердил'),
    ('Захарова Татьяна', '+7(910)222-22-33', 'zaharova@mail.ru', 'оплатил'),
    ('Крылов Павел', '+7(910)333-33-44', 'krylov@mail.ru', 'приглашён'),
    ('Максимова Юлия', '+7(910)444-44-55', 'maximova@mail.ru', 'подтвердил'),
    ('Астафьев Дмитрий', '+7(910)555-55-66', 'astafiev@mail.ru', 'приглашён'),
    ('Романова Светлана', '+7(910)666-66-77', 'romanova@mail.ru', 'оплатил'),
    ('Сергеев Иван', '+7(910)777-77-88', 'sergeev@mail.ru', 'подтвердил'),
    ('Тимофеева Елена', '+7(910)888-88-99', 'timofeeva@mail.ru', 'приглашён'),
    ('Григорьев Андрей', '+7(910)999-99-00', 'grigoriev@mail.ru', 'отказался'),
    ('Никитина Анастасия', '+7(910)000-00-11', 'nikitina@mail.ru', 'приглашён')
) AS g(full_name, phone, email, status)
CROSS JOIN (SELECT event_id FROM eventschema.event WHERE event_name = 'Юбилей директора ООО "Муромский завод"') ev
WHERE NOT EXISTS (SELECT 1 FROM eventschema.guest WHERE full_name = g.full_name AND event_id = ev.event_id);

INSERT INTO eventschema.event (event_name, event_date, budget, status, feedback, client_id, venue_id)
SELECT 
    'Свадьба Петровых',
    '2026-06-15',
    650000.00,
    'в процессе',
    NULL,
    (SELECT client_id FROM eventschema.client WHERE client_name = 'Семья Петровых'),
    (SELECT venue_id FROM eventschema.venue WHERE venue_name = 'Загородный клуб "Берёзка"')
WHERE NOT EXISTS (SELECT 1 FROM eventschema.event WHERE event_name = 'Свадьба Петровых');

INSERT INTO eventschema.task (description, deadline, status, event_id, responsible_contractor_id) VALUES
    ('Выбор даты и площадки', '2025-12-01', 'выполнено', (SELECT event_id FROM eventschema.event WHERE event_name = 'Свадьба Петровых'), NULL),
    ('Выбор фотографа', '2026-01-15', 'выполнено', (SELECT event_id FROM eventschema.event WHERE event_name = 'Свадьба Петровых'), NULL),
    ('Выбор ведущего', '2026-02-01', 'выполнено', (SELECT event_id FROM eventschema.event WHERE event_name = 'Свадьба Петровых'), NULL),
    ('Выбор декоратора', '2026-03-01', 'выполнено', (SELECT event_id FROM eventschema.event WHERE event_name = 'Свадьба Петровых'), NULL),
    ('Заказ кейтеринга', '2026-03-15', 'выполнено', (SELECT event_id FROM eventschema.event WHERE event_name = 'Свадьба Петровых'), NULL),  -- менеджер
    ('Согласование меню', '2026-04-20', 'выполнено', (SELECT event_id FROM eventschema.event WHERE event_name = 'Свадьба Петровых'), (SELECT contractor_id FROM eventschema.contractor WHERE contractor_name = 'Ресторан "Вкусно и точка"')),
    ('Оформление зала', '2026-06-01', 'в работе', (SELECT event_id FROM eventschema.event WHERE event_name = 'Свадьба Петровых'), (SELECT contractor_id FROM eventschema.contractor WHERE contractor_name = 'Студия декора "Арт-Стиль"')),
    ('Заказ торта', '2026-05-20', 'выполнено', (SELECT event_id FROM eventschema.event WHERE event_name = 'Свадьба Петровых'), NULL),  -- менеджер
    ('Рассылка приглашений', '2026-04-01', 'выполнено', (SELECT event_id FROM eventschema.event WHERE event_name = 'Свадьба Петровых'), NULL),
    ('Подтверждение явки гостей', '2026-06-01', 'в работе', (SELECT event_id FROM eventschema.event WHERE event_name = 'Свадьба Петровых'), NULL),
    ('Заключительная сверка', '2026-06-10', 'ожидает', (SELECT event_id FROM eventschema.event WHERE event_name = 'Свадьба Петровых'), NULL)
ON CONFLICT DO NOTHING;

INSERT INTO eventschema.expense (amount, expense_date, description, event_id, contractor_id, category) VALUES
    (60000.00, '2025-11-10', 'Аванс за аренду загородного клуба', (SELECT event_id FROM eventschema.event WHERE event_name = 'Свадьба Петровых'), NULL, 'аренда'),
    (40000.00, '2026-03-15', 'Аренда – доплата', (SELECT event_id FROM eventschema.event WHERE event_name = 'Свадьба Петровых'), NULL, 'аренда'),
    (10000.00, '2025-12-01', 'Аванс фотографу', (SELECT event_id FROM eventschema.event WHERE event_name = 'Свадьба Петровых'), (SELECT contractor_id FROM eventschema.contractor WHERE contractor_name = 'Фотостудия "Момент"'), 'фотограф'),
    (20000.00, '2026-01-10', 'Аванс ведущему', (SELECT event_id FROM eventschema.event WHERE event_name = 'Свадьба Петровых'), (SELECT contractor_id FROM eventschema.contractor WHERE contractor_name = 'Студия праздника "Виват"'), 'ведущий'),
    (50000.00, '2026-02-15', 'Предоплата кейтерингу', (SELECT event_id FROM eventschema.event WHERE event_name = 'Свадьба Петровых'), (SELECT contractor_id FROM eventschema.contractor WHERE contractor_name = 'Ресторан "Вкусно и точка"'), 'кейтеринг'),
    (15000.00, '2026-03-05', 'Аванс декоратору', (SELECT event_id FROM eventschema.event WHERE event_name = 'Свадьба Петровых'), (SELECT contractor_id FROM eventschema.contractor WHERE contractor_name = 'Студия декора "Арт-Стиль"'), 'декор'),
    (30000.00, '2026-04-25', 'Кейтеринг (вторая часть)', (SELECT event_id FROM eventschema.event WHERE event_name = 'Свадьба Петровых'), (SELECT contractor_id FROM eventschema.contractor WHERE contractor_name = 'Ресторан "Вкусно и точка"'), 'кейтеринг'),
    (150000.00, '2026-05-20', 'Кейтеринг (основной расчёт)', (SELECT event_id FROM eventschema.event WHERE event_name = 'Свадьба Петровых'), (SELECT contractor_id FROM eventschema.contractor WHERE contractor_name = 'Ресторан "Вкусно и точка"'), 'кейтеринг'),
    (5000.00, '2026-03-20', 'Типография (приглашения)', (SELECT event_id FROM eventschema.event WHERE event_name = 'Свадьба Петровых'), NULL, 'прочее'),
    (10000.00, '2026-05-10', 'Звукорежиссёр (аванс)', (SELECT event_id FROM eventschema.event WHERE event_name = 'Свадьба Петровых'), (SELECT contractor_id FROM eventschema.contractor WHERE contractor_name = 'Дмитрий Звук'), 'прочее'),
    (20000.00, '2026-05-25', 'Услуги стилиста', (SELECT event_id FROM eventschema.event WHERE event_name = 'Свадьба Петровых'), NULL, 'прочее'),
    (30000.00, '2026-06-01', 'Свадебный торт', (SELECT event_id FROM eventschema.event WHERE event_name = 'Свадьба Петровых'), (SELECT contractor_id FROM eventschema.contractor WHERE contractor_name = 'Ресторан "Вкусно и точка"'), 'кейтеринг')
ON CONFLICT (expense_id) DO NOTHING;

INSERT INTO eventschema.guest (full_name, contact_phone, contact_email, invitation_status, event_id)
SELECT g.full_name, g.phone, g.email, g.status, ev.event_id
FROM (VALUES
    ('Петров Иван', '+7(910)111-22-33', 'ivan.p@mail.ru', 'подтвердил'),
    ('Петрова Мария', '+7(910)222-33-44', 'maria.p@mail.ru', 'подтвердил'),
    ('Сидорова Анна', '+7(910)333-44-55', 'anna.s@mail.ru', 'подтвердил'),
    ('Кузнецов Алексей', '+7(910)444-55-66', 'alex.k@mail.ru', 'оплатил'),
    ('Смирнова Елена', '+7(910)555-66-77', 'elena.s@mail.ru', 'оплатил'),
    ('Михайлов Дмитрий', '+7(910)666-77-88', 'dmitry.m@mail.ru', 'оплатил'),
    ('Васильев Сергей', '+7(910)777-88-99', 'sergey.v@mail.ru', 'приглашён'),
    ('Фёдорова Ольга', '+7(910)888-99-00', 'olga.f@mail.ru', 'приглашён'),
    ('Николаев Игорь', '+7(910)999-00-11', 'igor.n@mail.ru', 'приглашён'),
    ('Александрова Татьяна', '+7(910)000-11-22', 'tatiana.a@mail.ru', 'приглашён'),
    ('Егоров Павел', '+7(910)111-22-34', 'pavel.e@mail.ru', 'приглашён'),
    ('Морозова Ирина', '+7(910)222-33-45', 'irina.m@mail.ru', 'приглашён'),
    ('Новиков Андрей', '+7(910)333-44-56', 'andrey.n@mail.ru', 'приглашён'),
    ('Ковалёва Светлана', '+7(910)444-55-67', 'svetlana.k@mail.ru', 'приглашён'),
    ('Лебедев Виктор', '+7(910)555-66-78', 'viktor.l@mail.ru', 'приглашён'),
    ('Соколова Анастасия', '+7(910)666-77-89', 'sokolova@mail.ru', 'подтвердил'),
    ('Тимофеев Артём', '+7(910)777-88-90', 'timofeev@mail.ru', 'оплатил'),
    ('Павлова Екатерина', '+7(910)888-99-01', 'pavlova@mail.ru', 'приглашён'),
    ('Фролов Денис', '+7(910)999-00-12', 'frolov@mail.ru', 'подтвердил'),
    ('Волкова Алина', '+7(910)000-11-23', 'volkova@mail.ru', 'приглашён')
) AS g(full_name, phone, email, status)
CROSS JOIN (SELECT event_id FROM eventschema.event WHERE event_name = 'Свадьба Петровых') ev
WHERE NOT EXISTS (SELECT 1 FROM eventschema.guest WHERE full_name = g.full_name AND event_id = ev.event_id);

INSERT INTO eventschema.event (event_name, event_date, budget, status, feedback, client_id, venue_id)
SELECT 
    'IT-конференция "Владимир-2026"',
    '2026-10-10',
    1200000.00,
    'планирование',
    NULL,
    (SELECT client_id FROM eventschema.client WHERE client_name = 'ИП Смирнов Алексей'),
    (SELECT venue_id FROM eventschema.venue WHERE venue_name = 'Конференц-холл "Премьер"')
WHERE NOT EXISTS (SELECT 1 FROM eventschema.event WHERE event_name = 'IT-конференция "Владимир-2026"');

INSERT INTO eventschema.task (description, deadline, status, event_id, responsible_contractor_id) VALUES
    ('Выбор и бронирование площадки', '2026-01-15', 'выполнено', (SELECT event_id FROM eventschema.event WHERE event_name = 'IT-конференция "Владимир-2026"'), NULL),
    ('Определение бюджета и спонсоров', '2026-02-01', 'выполнено', (SELECT event_id FROM eventschema.event WHERE event_name = 'IT-конференция "Владимир-2026"'), NULL),
    ('Поиск веб-студии для разработки сайта', '2026-03-01', 'выполнено', (SELECT event_id FROM eventschema.event WHERE event_name = 'IT-конференция "Владимир-2026"'), NULL),
    ('Поиск звукорежиссёра', '2026-07-01', 'ожидает', (SELECT event_id FROM eventschema.event WHERE event_name = 'IT-конференция "Владимир-2026"'), NULL),
    ('Формирование программы конференции', '2026-06-01', 'в работе', (SELECT event_id FROM eventschema.event WHERE event_name = 'IT-конференция "Владимир-2026"'), NULL),
    ('Приглашение спикеров', '2026-08-01', 'ожидает', (SELECT event_id FROM eventschema.event WHERE event_name = 'IT-конференция "Владимир-2026"'), NULL),
    ('Создание сайта конференции', '2026-05-01', 'выполнено', (SELECT event_id FROM eventschema.event WHERE event_name = 'IT-конференция "Владимир-2026"'), (SELECT contractor_id FROM eventschema.contractor WHERE contractor_name = 'Веб-студия "Айтишник"')),
    ('Рассылка приглашений участникам', '2026-09-01', 'ожидает', (SELECT event_id FROM eventschema.event WHERE event_name = 'IT-конференция "Владимир-2026"'), NULL),
    ('Организация трансляции', '2026-09-15', 'ожидает', (SELECT event_id FROM eventschema.event WHERE event_name = 'IT-конференция "Владимир-2026"'), (SELECT contractor_id FROM eventschema.contractor WHERE contractor_name = 'Студия звука "Акустика"')),
    ('Заказ раздаточных материалов', '2026-08-15', 'ожидает', (SELECT event_id FROM eventschema.event WHERE event_name = 'IT-конференция "Владимир-2026"'), NULL)
ON CONFLICT DO NOTHING;

INSERT INTO eventschema.expense (amount, expense_date, description, event_id, contractor_id, category) VALUES
    (100000.00, '2026-01-20', 'Бронирование конференц-зала (предоплата)', (SELECT event_id FROM eventschema.event WHERE event_name = 'IT-конференция "Владимир-2026"'), NULL, 'аренда'),
    (60000.00, '2026-05-01', 'Разработка сайта (оплата веб-студии)', (SELECT event_id FROM eventschema.event WHERE event_name = 'IT-конференция "Владимир-2026"'), (SELECT contractor_id FROM eventschema.contractor WHERE contractor_name = 'Веб-студия "Айтишник"'), 'прочее'),
    (15000.00, '2026-04-10', 'Закупка канцелярии', (SELECT event_id FROM eventschema.event WHERE event_name = 'IT-конференция "Владимир-2026"'), NULL, 'прочее')
ON CONFLICT DO NOTHING;

INSERT INTO eventschema.guest (full_name, contact_phone, contact_email, invitation_status, event_id)
SELECT g.full_name, g.phone, g.email, g.status, ev.event_id
FROM (VALUES
    ('Смирнов Алексей', '+7(910)111-11-11', 'alex.s@techno.ru', 'приглашён'),
    ('Кузнецова Мария', '+7(910)222-22-22', 'maria.k@techno.ru', 'приглашён'),
    ('Сидоров Дмитрий', '+7(910)333-33-33', 'dmitry.s@techno.ru', 'приглашён'),
    ('Васильева Елена', '+7(910)444-44-44', 'elena.v@techno.ru', 'приглашён'),
    ('Михайлов Игорь', '+7(910)555-55-55', 'igor.m@techno.ru', 'приглашён'),
    ('Фёдорова Ольга', '+7(910)666-66-66', 'olga.f@techno.ru', 'приглашён'),
    ('Николаев Павел', '+7(910)777-77-77', 'pavel.n@techno.ru', 'приглашён'),
    ('Александрова Татьяна', '+7(910)888-88-88', 'tatiana.a@techno.ru', 'приглашён'),
    ('Егоров Владимир', '+7(910)999-99-99', 'vladimir.e@techno.ru', 'приглашён'),
    ('Морозова Ирина', '+7(910)000-00-00', 'irina.m@techno.ru', 'приглашён')
) AS g(full_name, phone, email, status)
CROSS JOIN (SELECT event_id FROM eventschema.event WHERE event_name = 'IT-конференция "Владимир-2026"') ev
WHERE NOT EXISTS (SELECT 1 FROM eventschema.guest WHERE full_name = g.full_name AND event_id = ev.event_id);