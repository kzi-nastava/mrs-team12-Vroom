export interface HttpError {
  status: number;
  error?: { message?: string };
}

export function isHttpError(err: unknown): err is HttpError {
  return !!err && typeof (err as HttpError).status === 'number';
}