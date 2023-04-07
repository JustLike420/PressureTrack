FROM python:3.11

ENV PYTHONDONTWRITEBYTECODE=1
ENV PYTHONUNBUFFERED=1

WORKDIR /pressure

RUN pip install poetry
COPY poetry.lock pyproject.toml ./
RUN poetry install --no-root

COPY . .
