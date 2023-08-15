--
-- PostgreSQL database dump
--

-- Dumped from database version 13.3
-- Dumped by pg_dump version 13.3

-- Started on 2023-08-15 03:30:16 UTC

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 203 (class 1259 OID 17064)
-- Name: autenticador; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.autenticador (
    id bigint NOT NULL,
    identificador_credencial bytea NOT NULL,
    clave_publica bytea NOT NULL,
    id_usuario bigint,
    cuenta bigint NOT NULL,
    id_autenticador bytea,
    nombre character varying(255)
);


ALTER TABLE public.autenticador OWNER TO postgres;

--
-- TOC entry 3000 (class 0 OID 0)
-- Dependencies: 203
-- Name: TABLE autenticador; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE public.autenticador IS 'autenticadores para cada usuario';


--
-- TOC entry 202 (class 1259 OID 17062)
-- Name: autenticador_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.autenticador_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.autenticador_id_seq OWNER TO postgres;

--
-- TOC entry 3001 (class 0 OID 0)
-- Dependencies: 202
-- Name: autenticador_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.autenticador_id_seq OWNED BY public.autenticador.id;


--
-- TOC entry 201 (class 1259 OID 17053)
-- Name: usuario; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.usuario (
    id_usuario bigint NOT NULL,
    usuario character varying(155) NOT NULL,
    nombre character varying(255) NOT NULL,
    handle bytea NOT NULL
);


ALTER TABLE public.usuario OWNER TO postgres;

--
-- TOC entry 3002 (class 0 OID 0)
-- Dependencies: 201
-- Name: COLUMN usuario.usuario; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.usuario.usuario IS 'username';


--
-- TOC entry 3003 (class 0 OID 0)
-- Dependencies: 201
-- Name: COLUMN usuario.nombre; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.usuario.nombre IS 'nombre visible de usuario';


--
-- TOC entry 200 (class 1259 OID 17051)
-- Name: usuario_id_usuario_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.usuario_id_usuario_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.usuario_id_usuario_seq OWNER TO postgres;

--
-- TOC entry 3004 (class 0 OID 0)
-- Dependencies: 200
-- Name: usuario_id_usuario_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.usuario_id_usuario_seq OWNED BY public.usuario.id_usuario;


--
-- TOC entry 2859 (class 2604 OID 17067)
-- Name: autenticador id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.autenticador ALTER COLUMN id SET DEFAULT nextval('public.autenticador_id_seq'::regclass);


--
-- TOC entry 2858 (class 2604 OID 17056)
-- Name: usuario id_usuario; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.usuario ALTER COLUMN id_usuario SET DEFAULT nextval('public.usuario_id_usuario_seq'::regclass);


--
-- TOC entry 2863 (class 2606 OID 17072)
-- Name: autenticador pk_autenticador; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.autenticador
    ADD CONSTRAINT pk_autenticador PRIMARY KEY (id);


--
-- TOC entry 2861 (class 2606 OID 17061)
-- Name: usuario pk_usuario; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.usuario
    ADD CONSTRAINT pk_usuario PRIMARY KEY (id_usuario);


--
-- TOC entry 2864 (class 2606 OID 17073)
-- Name: autenticador fk_autenticador_usuario; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.autenticador
    ADD CONSTRAINT fk_autenticador_usuario FOREIGN KEY (id_usuario) REFERENCES public.usuario(id_usuario) ON UPDATE CASCADE ON DELETE RESTRICT;


-- Completed on 2023-08-15 03:30:16 UTC

--
-- PostgreSQL database dump complete
--

