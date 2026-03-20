export interface IServicio {
  id: number;
  nombre?: string | null;
  precio?: number | null;
}

export type NewServicio = Omit<IServicio, 'id'> & { id: null };
